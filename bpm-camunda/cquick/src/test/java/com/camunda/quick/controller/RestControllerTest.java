package com.camunda.quick.controller;

import com.camunda.quick.Runner;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.repository.DecisionDefinition;
import org.camunda.bpm.spring.boot.starter.property.CamundaBpmProperties;
import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 〈〉
 *
 * @author bob
 * @create 2020/9/12
 */
@SpringBootTest(classes = {Runner.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class RestControllerTest {

    private static final String CHECKORDER = "checkOrder";

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private CamundaBpmProperties camundaBpmProperties;

    @Autowired
    private RepositoryService repositoryService;

    @Test
    public void evaluateCheckOrder() throws JSONException {

        DecisionDefinition definition = repositoryService
                .createDecisionDefinitionQuery()
                .decisionDefinitionKey(CHECKORDER)
                .singleResult();
        assertThat(definition).isNotNull();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String inputBody = "{\n" +
                "  \"variables\" : {\n" +
                "    \"sum\" : { \"value\" : 900, \"type\" : \"Integer\" },\n" +
                "    \"status\" : { \"value\" : \"silver\", \"type\" : \"String\" }\n" +
                "  }\n" +
                "}";
        HttpEntity<String> request = new HttpEntity<>(inputBody, headers);

        final String check = testRestTemplate.postForObject("/engine-rest/engine/{engineName}/decision-definition/key/{key}/evaluate", request, String.class,
                camundaBpmProperties.getProcessEngineName(), CHECKORDER);

        assertThat(new JSONArray(check).getJSONObject(0).getJSONObject("result").getString("value")).isEqualTo("ok");
    }

}