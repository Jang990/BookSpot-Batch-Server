package com.bookspot.batch.web;

import com.bookspot.batch.job.launcher.LocalDateHolder;
import com.bookspot.batch.job.launcher.Top50BooksLauncher;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDate;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(Top50JobController.class)
class Top50JobControllerTest {
    @MockBean JpaMetamodelMappingContext jpaMetamodelMappingContext;
    @MockBean AuditorAware<String> auditorAware;

    @MockBean JobStatusService jobStatusService;
    @MockBean Top50BooksLauncher top50BooksLauncher;
    @MockBean LocalDateHolder localDateHolder;

    @Autowired
    MockMvc mvc;

    @Test
    void 같은_달을_요청하면_실패() throws Exception {
        when(localDateHolder.now())
                .thenReturn(LocalDate.of(2025, 8, 20));

        mvc.perform(
                        postRequest("/job/bookOpenSearch/top50/monthly", "2025-08-15")
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void 달의_수가_2_차이라면_실패() throws Exception {
        when(localDateHolder.now())
                .thenReturn(LocalDate.of(2025, 8, 20));

        mvc.perform(
                        postRequest("/job/bookOpenSearch/top50/monthly", "2025-06-29")
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void 그_주의_월요일을_기준으로_날짜가_지났으면_실패() throws Exception {
        when(localDateHolder.now())
                .thenReturn(LocalDate.of(2025, 8, 20));

        mvc.perform(
                        postRequest("/job/bookOpenSearch/top50/weekly", "2025-08-18")
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void 날짜가_5주_이상_차이나면_실패() throws Exception {
        when(localDateHolder.now())
                .thenReturn(LocalDate.of(2025, 8, 20));

        mvc.perform(postRequest("/job/bookOpenSearch/top50/weekly", "2025-07-16"));
    }

    @Test
    void 성공() throws Exception {
        when(localDateHolder.now())
                .thenReturn(LocalDate.of(2025, 8, 20));
        mvc.perform(postRequest("/job/bookOpenSearch/top50/monthly", "2025-07-01")).andExpect(status().isOk());
        mvc.perform(postRequest("/job/bookOpenSearch/top50/monthly", "2025-07-31")).andExpect(status().isOk());
        mvc.perform(postRequest("/job/bookOpenSearch/top50/weekly", "2025-07-17")).andExpect(status().isOk());
        mvc.perform(postRequest("/job/bookOpenSearch/top50/weekly", "2025-08-17")).andExpect(status().isOk());
    }

    private static MockHttpServletRequestBuilder postRequest(String path, String value) {
        return post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"referenceDateString\" : \"%s\" }".formatted(value));
    }
}