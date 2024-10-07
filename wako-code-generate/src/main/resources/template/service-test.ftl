package ${basePackage}.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author: ${author}
 * @date: ${date}
 */


@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@ImportResource({"classpath*:applicationContext.xml"})
@ActiveProfiles("development")
public class ${modelNameUpperCamel}Test {
    @SpyBean
    private BeanMapper beanMapper;
    @SpyBean
    private RedisService redisService;
    @SpyBean
    private CfgHandlerService cfgHandlerService;
    @SpyBean
    private ${modelNameUpperCamel} ${modelNameLowerCamel};
    <#list methodMockArgs as methodName,args>
    @Test
    public void test${methodName?cap_first}() throws Exception {
        ${methodMockProps[methodName]}
        ${modelNameLowerCamel}.${methodName}(${args});
    }
    </#list>
}
