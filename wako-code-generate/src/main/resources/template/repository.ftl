package ${MAPPER_PACKAGE};

import ${MODEL_PACKAGE}.${modelNameUpperCamel};
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.entity.Example;
import com.vip.hawkeye.service.PageReq;
import com.vip.wcs.hawkeye.dto.PageRes;
import org.apache.ibatis.session.RowBounds;
import java.util.List;
/**
 * @author: ${author}
 * @date: ${date}
 */
@Repository
public class ${modelNameUpperCamel}TkRepository extends BaseRepository<${modelNameUpperCamel}>{

    public List<${modelNameUpperCamel}> selectBy(${modelNameUpperCamel} cond) {
        AssertUtils.notNull(cond, "${modelNameUpperCamel}TkRepository.selectBy cond is null");
        Example example = Example.builder(${modelNameUpperCamel}.class).build();
        Example.Criteria criteria = example.createCriteria();
        wrap(cond,criteria,"class");
        criteria.andEqualTo(${modelNameUpperCamel}.PROP_isDeleted, DeleteType.UN_DELETED.getCode());
        return selectByExample(example);
    }

}
