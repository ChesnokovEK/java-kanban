package Manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @BeforeEach
    public void beforeEach() {
        manager = new InMemoryTaskManager();
    }

    @Test
    public void shouldCreateInMemoryTaskManager() {
        assertNotNull(manager.getAllTasks());
    }
}
