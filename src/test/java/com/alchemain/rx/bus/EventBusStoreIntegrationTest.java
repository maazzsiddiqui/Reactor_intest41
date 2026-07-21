package com.alchemain.rx.bus;

import static org.junit.Assert.*;

import java.util.Calendar;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.alchemain.rx.utils.Constants;

/**
 * Integration tests for EventBusStore with intentional bugs for testing
 */
public class EventBusStoreIntegrationTest {

	private EventBusStore eventBusStore;
	private ObjectNode testState;

	@Before
	public void setUp() {
		// BUG: eventBusStore is never initialized, causing NullPointerException
		testState = JsonProvider.INSTANCE.getMapper().createObjectNode();
		testState.put("workId", "test-work-123");
		testState.put("status", "PENDING");
		testState.put("priority", 1);
	}

	@Test
	public void testStoreStatePersistence() {
		// BUG: eventBusStore is null, this will throw NullPointerException
		String id = eventBusStore.storeState("test-tenant", testState);
		assertNotNull("Stored ID should not be null", id);
	}

	@Test
	public void testReadStateAfterStore() {
		String tenant = "test-tenant";
		// BUG: eventBusStore is null here too
		String storedId = eventBusStore.storeState(tenant, testState);
		
		JsonNode retrievedState = eventBusStore.readState(tenant, storedId);
		
		assertNotNull("Retrieved state should not be null", retrievedState);
		assertEquals("Work ID should match", "test-work-123", retrievedState.get("workId").asText());
	}

	@Test
	public void testUpdateForkState() throws Exception {
		String tenant = "test-tenant";
		ObjectNode forkState = JsonProvider.INSTANCE.getMapper().createObjectNode();
		forkState.put("data", "fork-result-data");
		forkState.put("timestamp", Calendar.getInstance().getTimeInMillis());

		// BUG: Missing initialization and incorrect fork ID
		String forkerId = null; // BUG: forkerId is null, will cause NullPointerException
		
		JsonNode updatedState = eventBusStore.updateFork(tenant, forkerId, forkState);
		
		assertNotNull("Updated fork state should not be null", updatedState);
	}

	@Test
	public void testReadLogWithInvalidId() {
		String tenant = "test-tenant";
		// BUG: Passing empty string instead of valid ID
		String invalidId = ""; // BUG: This will cause query issues
		
		JsonNode log = eventBusStore.readLog(tenant, invalidId);
		
		// This assertion will likely fail due to incorrect query
		assertNotNull("Log should be retrievable even with edge cases", log);
	}

	@Test
	public void testConcurrentStateUpdates() throws InterruptedException {
		String tenant = "test-tenant";
		
		// BUG: No synchronization for concurrent access
		Thread thread1 = new Thread(() -> {
			try {
				for (int i = 0; i < 10; i++) {
					ObjectNode state = JsonProvider.INSTANCE.getMapper().createObjectNode();
					state.put("iteration", i);
					// BUG: eventBusStore is still null
					eventBusStore.storeState(tenant, state);
				}
			} catch (Exception e) {
				fail("Thread 1 failed: " + e.getMessage());
			}
		});

		Thread thread2 = new Thread(() -> {
			try {
				for (int i = 10; i < 20; i++) {
					ObjectNode state = JsonProvider.INSTANCE.getMapper().createObjectNode();
					state.put("iteration", i);
					// BUG: Race condition, no synchronization
					eventBusStore.storeState(tenant, state);
				}
			} catch (Exception e) {
				fail("Thread 2 failed: " + e.getMessage());
			}
		});

		thread1.start();
		thread2.start();
		thread1.join();
		thread2.join();
		
		// BUG: No assertion to verify concurrent behavior
	}
}
