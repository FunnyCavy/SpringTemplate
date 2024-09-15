package com.dxmy.template.common.cache;

import org.redisson.api.*;

/**
 * Redis 服务接口
 */
@SuppressWarnings("unused")
public interface RedisService {

    /**
     * 设置指定 key 的值
     *
     * @param key   键
     * @param value 值
     * @param <T>   值的类型
     */
    <T> void setValue(String key, T value);

    /**
     * 设置指定 key 的值, 并指定过期时间
     *
     * @param key     键
     * @param value   值
     * @param expired 过期时间 (毫秒)
     * @param <T>     值的类型
     */
    <T> void setValue(String key, T value, long expired);

    /**
     * 获取指定 key 的值
     *
     * @param key 键
     * @param <T> 值的类型
     * @return 值, 若 key 不存在则返回 null
     */
    <T> T getValue(String key);

    /**
     * 获取队列
     *
     * @param key 键
     * @param <T> 队列中元素的类型
     * @return 队列
     */
    <T> RQueue<T> getQueue(String key);

    /**
     * 获取加锁队列
     *
     * @param key 键
     * @param <T> 队列中元素的类型
     * @return 加锁队列
     */
    <T> RBlockingQueue<T> getBlockingQueue(String key);

    /**
     * 获取延迟队列
     *
     * @param blockingQueue 加锁队列
     * @param <T>           队列中元素的类型
     * @return 延迟队列
     */
    <T> RDelayedQueue<T> getDelayedQueue(RBlockingQueue<T> blockingQueue);

    /**
     * 使指定 key 的值自增
     *
     * @param key 键
     * @return 自增后的值
     */
    long incr(String key);

    /**
     * 使指定 key 的值自增 (指定变化量)
     *
     * @param key   键
     * @param delta 变化量
     * @return 自增后的值
     */
    long incrBy(String key, long delta);

    /**
     * 使指定 key 的值自减
     *
     * @param key 键
     * @return 自减后的值
     */
    long decr(String key);

    /**
     * 使指定 key 的值自减 (指定变化量)
     *
     * @param key   键
     * @param delta 变化量
     * @return 自减后的值
     */
    long decrBy(String key, long delta);

    /**
     * 移除指定 key 的值
     *
     * @param key 键
     */
    void remove(String key);

    /**
     * 判断指定 key 是否存在
     *
     * @param key 键
     * @return 指定 key 是否存在
     */
    boolean isExists(String key);

    /**
     * 将指定的值添加到集合中
     *
     * @param key   键
     * @param value 值
     */
    void addToSet(String key, String value);

    /**
     * 判断指定的值是否为集合的成员
     *
     * @param key   键
     * @param value 值
     * @return 指定的值是否为集合的成员
     */
    boolean isSetMember(String key, String value);

    /**
     * 将指定的值添加到列表中
     *
     * @param key   键
     * @param value 值
     */
    void addToList(String key, String value);

    /**
     * 获取列表中指定索引的值
     *
     * @param key   键
     * @param index 索引
     * @return 列表中指定索引的值, 若索引超出范围则返回 null
     */
    String getFromList(String key, int index);

    /**
     * 获取哈希表
     *
     * @param key 键
     * @param <K> 字段的类型
     * @param <V> 值的类型
     * @return 哈希表
     */
    <K, V> RMap<K, V> getMap(String key);

    /**
     * 将指定的键值对添加到哈希表中
     *
     * @param key   键
     * @param field 字段
     * @param value 值
     */
    void addToMap(String key, String field, String value);

    /**
     * 获取哈希表中指定字段的值
     *
     * @param key   键
     * @param field 字段
     * @return 哈希表中指定字段的值, 若字段不存在则返回 null
     */
    String getFromMap(String key, String field);

    /**
     * 获取哈希表中指定字段的值
     *
     * @param key   键
     * @param field 字段
     * @param <K>   字段的类型
     * @param <V>   值的类型
     * @return 哈希表中指定字段的值, 若字段不存在则返回 null
     */
    <K, V> V getFromMap(String key, K field);

    /**
     * 将指定的值添加到有序集合中
     *
     * @param key   键
     * @param value 值
     */
    void addToSortedSet(String key, String value);

    /**
     * 获取 Redis 锁 (可重入锁)
     *
     * @param key 键
     * @return 锁对象
     */
    RLock getLock(String key);

    /**
     * 获取 Redis 锁 (公平锁)
     *
     * @param key 键
     * @return 锁对象
     */
    RLock getFairLock(String key);

    /**
     * 获取 Redis 锁 (读写锁)
     *
     * @param key 键
     * @return 读写锁对象
     */
    RReadWriteLock getReadWriteLock(String key);

    /**
     * 获取 Redis 信号量
     *
     * @param key 键
     * @return Redis 信号量对象
     */
    RSemaphore getSemaphore(String key);

    /**
     * 获取 Redis 可过期信号量
     *
     * @param key 键
     * @return Redis 可过期信号量对象
     */
    RPermitExpirableSemaphore getPermitExpirableSemaphore(String key);

    /**
     * 获取 Redis 闭锁
     *
     * @param key 键
     * @return Redis 闭锁对象
     */
    RCountDownLatch getCountDownLatch(String key);

    /**
     * 获取 Redis 布隆过滤器
     *
     * @param key 键
     * @param <T> 处理值的类型
     * @return Redis 布隆过滤器对象
     */
    <T> RBloomFilter<T> getBloomFilter(String key);

}
