package com.glyde.mall.batch.job.sample;

import java.util.List;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.glyde.mall.batch.job.sample.entity.TestDto;
import com.glyde.mall.batch.job.sample.mapper.TestMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * The type Sample tasklet.
 */
@Slf4j
public class SampleTasklet implements Tasklet {

	@Autowired
	@Qualifier("sqlSessionFactoryGlyde")
	SqlSessionFactory sqlSessionFactory;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

		/**
		 *  현재, 아래 tasklet의 처리내용(select 후 update)은 나쁜 사례(BAD CASE)
		 *  tasklet에서 처리해야 내용은 storedprocedure 호출, 단순파일생성, select가 없는 단순 insert,update,delete만 추천.
		 *  DB select가 필요한 내용들은 모두 read/process/write 구조로 처리하기 바람.
		 */
		
		try {
			List<TestDto> list = sqlSessionFactory.openSession().getMapper(TestMapper.class).select();
			for (TestDto dto : list) {
				TestDto result = new TestDto();
				result.setId(dto.getId());
				result.setName(dto.getName().toLowerCase());
				if (sqlSessionFactory.openSession().getMapper(TestMapper.class).update(result) < 1) {
					throw new RuntimeException();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return RepeatStatus.FINISHED;
	}
}
