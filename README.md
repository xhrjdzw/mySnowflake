# mySnowflake
提供多种全局唯一ID生成器的算法,适用于不同的部署环境(单库,多库,集群)
##mySnowflake组件提供对实体唯一标识生成的统一封装
包含普通的UUID、基于Redis的自增ID、和snowflake、oid算法等。
组件提供统一接口对OID进行生成，通过配置的方式实现对几种策略的切换。
同时支持对自定义ID生成方案的扩展。
(redis缓存部分未完成createJedisPool)
