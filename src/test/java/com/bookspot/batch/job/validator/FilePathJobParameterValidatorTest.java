package com.bookspot.batch.job.validator;

import com.bookspot.batch.global.JobParameterHelper;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;

import static org.junit.jupiter.api.Assertions.*;

class FilePathJobParameterValidatorTest {

    @Test
    void 필수_파라미터_누락시_예외발생() {
        assertThrows(JobParametersInvalidException.class, () -> FilePathJobParameterValidator.onlyRootDir()
                .validate(new JobParametersBuilder().toJobParameters())
        );

        assertThrows(JobParametersInvalidException.class, () -> FilePathJobParameterValidator.rootDirAndAggregatedFile()
                .validate(new JobParametersBuilder().toJobParameters())
        );
    }

    @Test
    void onlyRootDir에_디렉토리_정보를_넘기지_않으면_예외발생() {
        FilePathJobParameterValidator validator = FilePathJobParameterValidator.onlyRootDir();
        JobParameters jobParameters = new JobParametersBuilder().toJobParameters();

        JobParametersInvalidException exception = assertThrows(JobParametersInvalidException.class, () -> validator.validate(jobParameters));
        assertEquals("rootDirPath 는 필수 JobParameter입니다.", exception.getMessage());
    }

    @Test
    void onlyRootDir에_파일을_파라미터를_디렉토리로_넘기면_예외발생() {
        FilePathJobParameterValidator validator = FilePathJobParameterValidator.onlyRootDir();


        JobParametersInvalidException exception = assertThrows(JobParametersInvalidException.class,
                () -> validator.validate(
                        JobParameterHelper.addRootDirPath(
                                new JobParametersBuilder(),
                                "src/test/resources/files/sample/stockSync/10001_2025-03-01.csv"
                        ).toJobParameters())
        );
        assertEquals("rootDirPath는 디렉토리 경로여야 합니다.", exception.getMessage());
    }

    @Test
    void onlyRootDir에_디렉토리를_넘기면_정상처리() throws JobParametersInvalidException {
        FilePathJobParameterValidator validator = FilePathJobParameterValidator.onlyRootDir();

        validator.validate(
                JobParameterHelper.addRootDirPath(
                        new JobParametersBuilder(),
                        "src/test/resources/files/sample/stockSync"
                ).toJobParameters()
        );
    }

    @Test
    void onlyAggregatedFile에_파일_정보를_넘기지_않으면_예외발생() {
        FilePathJobParameterValidator validator = FilePathJobParameterValidator.onlyAggregatedFile();
        JobParameters jobParameters = new JobParametersBuilder().toJobParameters();

        JobParametersInvalidException exception = assertThrows(JobParametersInvalidException.class, () -> validator.validate(jobParameters));
        assertEquals("aggregatedFilePath 는 필수 JobParameter입니다.", exception.getMessage());
    }

    @Test
    void onlyAggregatedFile에_파일을_파라미터를_디렉토리로_넘기면_예외발생() throws JobParametersInvalidException {
        FilePathJobParameterValidator validator = FilePathJobParameterValidator.onlyAggregatedFile();

        JobParametersInvalidException exception = assertThrows(JobParametersInvalidException.class,
                () -> validator.validate(
                        JobParameterHelper.addAggregatedFilePath(
                                new JobParametersBuilder(),
                                "src/test/resources/files/sample/stockSync"
                        ).toJobParameters())
        );
        assertEquals("aggregatedFilePath는 파일 경로여야 합니다.", exception.getMessage());
    }

    @Test
    void onlyAggregatedFile에_파일을_넘기면_정상처리() throws JobParametersInvalidException {
        FilePathJobParameterValidator validator = FilePathJobParameterValidator.onlyAggregatedFile();


        validator.validate(
                JobParameterHelper.addAggregatedFilePath(
                        new JobParametersBuilder(),
                        "src/test/resources/files/sample/stockSync/10001_2025-03-01.csv"
                ).toJobParameters()
        );
    }

    @Test
    void onlyAggregatedFile에_존재하지_않는_파일이여도_파일형식의_Path면_정상처리() throws JobParametersInvalidException {
        FilePathJobParameterValidator validator = FilePathJobParameterValidator.onlyAggregatedFile();


        validator.validate(
                JobParameterHelper.addAggregatedFilePath(
                        new JobParametersBuilder(),
                        "src/test/resources/files/sample/stockSync/something.csv"
                ).toJobParameters()
        );
    }

    @Test
    void dirFile에_파일이_누락되면_예외발생() {
        FilePathJobParameterValidator validator = FilePathJobParameterValidator.rootDirAndAggregatedFile();

        JobParametersInvalidException exception = assertThrows(JobParametersInvalidException.class,
                () -> validator.validate(
                        JobParameterHelper.addRootDirPath(
                                new JobParametersBuilder(),
                                "src/test/resources/files/sample/stockSync"
                        ).toJobParameters()
                )
        );

        assertEquals("aggregatedFilePath 는 필수 JobParameter입니다.", exception.getMessage());
    }

    @Test
    void dirFile에_디렉토리가_누락되면_예외발생() {
        FilePathJobParameterValidator validator = FilePathJobParameterValidator.rootDirAndAggregatedFile();

        JobParametersInvalidException exception = assertThrows(JobParametersInvalidException.class,
                () -> validator.validate(
                        JobParameterHelper.addAggregatedFilePath(
                                new JobParametersBuilder(),
                                "src/test/resources/files/sample/stockSync/10001_2025-03-01.csv"
                        ).toJobParameters()
                )
        );

        assertEquals("rootDirPath 는 필수 JobParameter입니다.", exception.getMessage());
    }

    @Test
    void dirFile에_디렉토리_파리미터를_파일로_넘기면_예외발생() throws JobParametersInvalidException {
        FilePathJobParameterValidator validator = FilePathJobParameterValidator.rootDirAndAggregatedFile();
        JobParametersBuilder builder = new JobParametersBuilder();
        JobParameterHelper.addRootDirPath(builder, "src/test/resources/files/sample/stockSync/10001_2025-03-01.csv");
        JobParameterHelper.addAggregatedFilePath(builder, "src/test/resources/files/sample/stockSync/10001_2025-03-01.csv");

        JobParametersInvalidException exception = assertThrows(JobParametersInvalidException.class, () -> validator.validate(builder.toJobParameters()));
        assertEquals("rootDirPath는 디렉토리 경로여야 합니다.", exception.getMessage());
    }

    @Test
    void dirFile에_디렉토리와_파일이_들어오면_정상처리() throws JobParametersInvalidException {
        FilePathJobParameterValidator validator = FilePathJobParameterValidator.rootDirAndAggregatedFile();
        JobParametersBuilder builder = new JobParametersBuilder();
        JobParameterHelper.addRootDirPath(builder, "src/test/resources/files/sample/stockSync");
        JobParameterHelper.addAggregatedFilePath(builder, "src/test/resources/files/sample/stockSync/10001_2025-03-01.csv");

        validator.validate(builder.toJobParameters());
    }

}