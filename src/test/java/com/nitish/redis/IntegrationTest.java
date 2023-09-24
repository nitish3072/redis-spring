package com.nitish.redis;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = TestRedisConfiguration.class)
@TestPropertySource(locations = {"classpath:application.properties"})
public class IntegrationTest {

	@Mock
	private StudentDataRedisListOperationDao<StudentRedisEntity> dataRedisListOperationDao;

	@Test
	public void saveToRedis() {
		UUID id = UUID.randomUUID();
		StudentRedisEntity studentRedisEntity = new StudentRedisEntity();
		studentRedisEntity.setPhone("78738183728");
		studentRedisEntity.setEmail("test@test.com");
		studentRedisEntity.setFirstname("Testfirst");
		studentRedisEntity.setLastname("Testlast");
		studentRedisEntity.setUuid(id.toString());
		dataRedisListOperationDao.addToHash(studentRedisEntity);
		StudentRedisEntity fetched = dataRedisListOperationDao.getData(id.toString());
		assertEquals(fetched.getUuid(), id.toString());
	}

}
