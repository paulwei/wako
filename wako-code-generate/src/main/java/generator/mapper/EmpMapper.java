package generator.mapper;

import generator.domain.Emp;

/**
* @author PAUL
* @description 针对表【emp】的数据库操作Mapper
* @createDate 2023-03-12 10:54:19
* @Entity generator.domain.Emp
*/
public interface EmpMapper {

    int deleteByPrimaryKey(Long id);

    int insert(Emp record);

    int insertSelective(Emp record);

    Emp selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Emp record);

    int updateByPrimaryKey(Emp record);

}
