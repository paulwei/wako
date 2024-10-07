package ${basePackage}.service.impl;

import ${MODEL_PACKAGE}.${modelNameUpperCamel};
import ${MAPPER_PACKAGE}.${modelNameUpperCamel}TkRepository;
import ${basePackage}.service.${modelNameUpperCamel}BizService;
import com.vip.venus.data.common.annotation.RepositorySharding;
import com.vip.wcs.kernel.util.AssertUtils;
import com.vip.wcs.kernel.util.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import com.vip.wcs.kernel.util.NumberUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import java.util.Date;
import java.util.List;
import static com.vip.wcs.hawkeye.utils.Constants.MAX_REMARK_LEN;
import static com.vip.wcs.hawkeye.utils.Constants.MIN_REMARK_LEN;
import com.vip.wcs.hawkeye.utils.Constants;
import com.vip.hawkeye.service.PageReq;
import com.vip.wcs.hawkeye.dto.PageRes;
/**
 * @author: ${author}
 * @date: ${date}
 */
@Service
public class ${modelNameUpperCamel}BizServiceImpl  implements ${modelNameUpperCamel}BizService {
    protected static final Logger logger = LoggerFactory.getLogger(${modelNameUpperCamel}BizServiceImpl.class);

    @Autowired
    private ${modelNameUpperCamel}TkRepository ${modelNameLowerCamel}TkRepository;

    @RepositorySharding(strategy = "hawk_departdatabase", key = "#departdatabase")
    @Override
    public int createSelective(${modelNameUpperCamel} entity, String departdatabase) {
        int cnt = 0;
        try {
            checkCreateArgs(entity);
            entity.withId(null)
            .withCreateTime(new Date())
            .withUpdateTime(new Date())
            .withCreateBy(BeanUtils.isNotEmpty(entity.getCreateBy())? entity.getCreateBy():Constants.SYS_USE)
            .withErrMsg(StringUtils.substring(entity.getErrMsg(), MIN_REMARK_LEN, MAX_REMARK_LEN));
            cnt = ${modelNameLowerCamel}TkRepository.insertSelective(entity);
            logger.info("${modelNameUpperCamel}BizService createSelective entity:{},cnt:{} departdatabase:{}",entity,cnt,departdatabase);
        } catch (Exception e) {
            logger.error("${modelNameUpperCamel}BizService createSelective exception entity:",e);
        }
        return cnt;
    }

    @RepositorySharding(strategy = "hawk_departdatabase", key = "#departdatabase")
    @Override
    public int updateSelective(${modelNameUpperCamel} entity, String departdatabase) {
        int cnt = 0;
        try {
            checkUpdateArgs(entity);
            entity.withCreateTime(null)
            .withCreateBy(null)
            .withUpdateTime(new Date())
            .withUpdateBy(BeanUtils.isNotEmpty(entity.getUpdateBy())? entity.getUpdateBy():Constants.SYS_USE)
            .withErrMsg(StringUtils.substring(entity.getErrMsg(), MIN_REMARK_LEN, MAX_REMARK_LEN));
            cnt = ${modelNameLowerCamel}TkRepository.updateByPrimaryKeySelective(entity);
            logger.info("${modelNameUpperCamel}BizService updateSelective entity:{},cnt:{} departdatabase:{}",entity,cnt,departdatabase);
        } catch (Exception e) {
            logger.error("${modelNameUpperCamel}BizService updateSelective exception entity:"+entity,e);
        }
        return cnt;
    }

    @RepositorySharding(strategy = "hawk_departdatabase", key = "#departdatabase")
    @Override
    public List<${modelNameUpperCamel}> selectBy(${modelNameUpperCamel} cond, String departdatabase) {
        try {
            List<${modelNameUpperCamel}> list = ${modelNameLowerCamel}TkRepository.selectBy(cond);
            logger.info("${modelNameUpperCamel}BizService selectBy cond:{},list:{} departdatabase:{}",cond,JSON.toJSONString(list),departdatabase);
            return list;
        } catch (Exception e) {
            logger.error("${modelNameUpperCamel}BizService selectBy exception cond:"+cond,e);
            return Lists.newArrayList();
        }
    }

    @RepositorySharding(strategy = "hawk_departdatabase", key = "#departdatabase")
    @Override
    public ${modelNameUpperCamel} selectById(Long id, String departdatabase) {
        try {
            AssertUtils.notNull(id, "id must not null");
            ${modelNameUpperCamel} entity = ${modelNameLowerCamel}TkRepository.selectByPrimaryKey(id);
            logger.info("${modelNameUpperCamel}BizService selectById id:{},plan:{} departdatabase:{}", id, plan, departdatabase);
            return entity;
        } catch (InspectionException e) {
            logger.error("${modelNameUpperCamel}BizService selectById exception id:" + id, e);
            throw e;
        } catch (IllegalArgumentException e) {
            logger.error("${modelNameUpperCamel}BizService selectById exception id:" + id, e);
            throw new InspectionException(InspectionErrCode.PARAM_ILLEGAL.getCode(), e.getMessage());
        } catch (Exception e) {
            logger.error("${modelNameUpperCamel}BizService selectById exception id:" + id, e);
            throw new InspectionException(InspectionErrCode.SYSTEM_ERROR.getCode(), InspectionErrCode.SYSTEM_ERROR.getDesc());
        }
    }

    @RepositorySharding(strategy = "hawk_departdatabase", key = "#departdatabase")
    @Override
    public PageRes<${modelNameUpperCamel}> selectPageBy(${modelNameUpperCamel} cond, PageReq pageReq, String departdatabase) {
        try {
            PageRes<${modelNameUpperCamel}> pageRes = ${modelNameLowerCamel}TkRepository.selectPageBy(cond,pageReq);
            logger.info("${modelNameUpperCamel}BizService selectPageBy cond:{},pageReq:{},data size:{} departdatabase:{}",cond,pageReq,CollectionUtils.size(pageRes.getData()),departdatabase);
            return pageRes;
        } catch (Exception e) {
            logger.error("${modelNameUpperCamel}BizService selectPageBy exception cond:"+cond+",pageReq:"+pageReq,e);
            return PageRes.<${modelNameUpperCamel}>builder()
                .pageIndex(pageReq!=null?pageReq.getPageIndex():0)
                .pageSize(pageReq!=null?pageReq.getPageSize():0)
                .build();
        }
    }

    private void checkCreateArgs(${modelNameUpperCamel} entity){
        AssertUtils.notNull(entity,"checkCreateArgs entity must not null");
    }

    private void checkUpdateArgs(${modelNameUpperCamel} entity){
        AssertUtils.notNull(entity,"checkUpdateArgs entity must not null");
        AssertUtils.isTrue(NumberUtils.isGreaterThanZero(entity.getId()),"checkUpdateArgs entity[id] must greater than zero");
    }


}
