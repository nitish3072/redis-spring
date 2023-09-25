package com.nitish.redis;

import com.nitish.redis.config.RedisConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { TestRedisConfiguration.class, RedisProperties.class, RedisConfig.class, StudentDataRedisListOperationDaoImpl.class })
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {TestRedisConfiguration.class})
@TestPropertySource(locations = {"classpath:application.properties"})
public class IntegrationTest {

	@Autowired
	private StudentDataRedisListOperationDao<StudentRedisEntity> dataRedisListOperationDao;

	UUID id = UUID.randomUUID();

	@Test
	public void saveAndGetToRedis() {
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

	@Test
	public void deleteDataRedis() {
		StudentRedisEntity studentRedisEntity = new StudentRedisEntity();
		studentRedisEntity.setPhone("78738183728");
		studentRedisEntity.setEmail("test@test.com");
		studentRedisEntity.setFirstname("Testfirst");
		studentRedisEntity.setLastname("Testlast");
		studentRedisEntity.setUuid(id.toString());
		dataRedisListOperationDao.addToHash(studentRedisEntity);
		dataRedisListOperationDao.deleteData(id.toString());
		StudentRedisEntity fetched = dataRedisListOperationDao.getData(id.toString());
		assertNull(fetched);
	}

}
