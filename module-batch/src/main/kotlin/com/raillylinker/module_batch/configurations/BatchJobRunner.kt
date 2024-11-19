package com.raillylinker.module_batch.configurations

import org.springframework.batch.core.launch.JobLauncher
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class BatchJobRunner(
    private val jobLauncher: JobLauncher,
    private val job: Job // TestJobConfiguration에서 설정한 job 빈
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        try {
            // Job 실행
            val jobParameters = JobParametersBuilder()
                .addLong("time", System.currentTimeMillis()) // 매 실행 시 고유한 파라미터를 추가하여 중복 실행 방지
                .toJobParameters()
            jobLauncher.run(job, jobParameters)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
