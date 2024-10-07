package ${basePackage}.service;

import ${MODEL_PACKAGE}.${modelNameUpperCamel};
import com.vip.hawkeye.service.PageReq;
import com.vip.wcs.hawkeye.dto.PageRes;
import java.util.List;

/**
 * @author: ${author}
 * @date: ${date}
 */
public interface ${modelNameUpperCamel}BizService {
 int createSelective(${modelNameUpperCamel} entity, String departdatabase);
 int updateSelective(${modelNameUpperCamel} entity, String departdatabase);
 ${modelNameUpperCamel} selectById(Long id, String departdatabase);
 List<${modelNameUpperCamel}> selectBy(${modelNameUpperCamel} cond, String departdatabase);
 PageRes<${modelNameUpperCamel}> selectPageBy(${modelNameUpperCamel} cond, PageReq pageReq, String departdatabase);
}
