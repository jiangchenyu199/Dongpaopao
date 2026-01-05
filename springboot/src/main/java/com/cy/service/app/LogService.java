package com.cy.service.app;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cy.common.R;
import com.cy.entity.app.Log;
import java.util.Map;

/**
 * 日志服务接口
 * 继承IService，使用MyBatis-Plus提供的增强服务能力
 */
public interface LogService extends IService<Log> {

    /**
     * 异步保存日志
     * @param log 日志实体
     */
    void saveLog(Log log);

    /**
     * 分页查询日志
     * @param params 查询参数
     * @return 分页结果
     */
    R queryLogPage(Map<String, Object> params);

    /**
     * 清理指定天数前的日志
     * @param days 天数
     */
    void cleanLog(int days);
}