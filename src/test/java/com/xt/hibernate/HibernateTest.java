package com.xt.hibernate;

import com.xt.hibernate.bean.News;
import com.xt.hibernate.bean.Pay;
import com.xt.hibernate.bean.Worker;
import com.xt.hibernate.n21.Customer;
import com.xt.hibernate.n21.Order;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.jdbc.Work;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;


/**
 * @author xt
 * @create 2019/4/3 11:17
 * @Desc
 */
public class HibernateTest {

    private SessionFactory sessionFactory;
    private Session session;
    private Transaction transaction;

    @Before
    public void init() {
        // 创建 Configuration 对象: 对应 hibernate 的基本配置信息和 对象关系映射信息
        Configuration configuration = new Configuration().configure();

        //1. 创建一个 SessionFactory 对象
        sessionFactory = configuration.buildSessionFactory();

        //2. 创建一个 Session 对象
        session = sessionFactory.openSession();

        //3. 开启事务
        transaction = session.beginTransaction();
    }

    @After
    public void destroy() {
        //5. 提交事务
        transaction.commit();
        //6. 关闭 Session
        session.close();
        //7. 关闭 SessionFactory 对象
        sessionFactory.close();
    }

    @Test
    public void testN21Delete(){
        //在不设定级联关系的情况下, 且 1 这一端的对象有 n 的对象在引用, 不能直接删除 1 这一端的对象
        Customer customer = session.get(Customer.class, 1);
        session.delete(customer);
    }

    @Test
    public void testN21Update(){
        Order order = session.get(Order.class, 1);
        order.getCustomer().setCustomerName("AAA");
    }

    @Test
    public void testMany2OneGet(){
        //1. 若查询多的一端的一个对象, 则默认情况下, 只查询了多的一端的对象. 而没有查询关联的
        //1 的那一端的对象!
        Order order = (Order) session.get(Order.class, 1);
        System.out.println(order.getOrderName());

        System.out.println(order.getCustomer().getClass().getName());

        session.close();

        //2. 在需要使用到关联的对象时, 才发送对应的 SQL 语句.
        Customer customer = order.getCustomer();
        System.out.println(customer.getCustomerName());

        //3. 在查询 Customer 对象时, 由多的一端导航到 1 的一端时,
        //若此时 session 已被关闭, 则默认情况下会发生 LazyInitializationException 异常

        //4. 获取 Order 对象时, 默认情况下, 其关联的 Customer 对象是一个代理对象!

    }

    @Test
    public void testN21 () {
        Order order1 = new Order();
        order1.setOrderName("O-1");

        Order order2 = new Order();
        order2.setOrderName("O-2");

        Customer customer = new Customer();
        customer.setCustomerName("C-1");

        order1.setCustomer(customer);
        order2.setCustomer(customer);

        //执行  save 操作: 先插入 Customer, 再插入 Order, 3 条 INSERT
        //先插入 1 的一端, 再插入 n 的一端, 只有 INSERT 语句.
//		session.save(customer);
//
//		session.save(order1);
//		session.save(order2);

        //先插入 Order, 再插入 Customer. 3 条 INSERT, 2 条 UPDATE
        //先插入 n 的一端, 再插入 1 的一端, 会多出 UPDATE 语句!
        //因为在插入多的一端时, 无法确定 1 的一端的外键值. 所以只能等 1 的一端插入后, 再额外发送 UPDATE 语句.
        //推荐先插入 1 的一端, 后插入 n 的一端

        session.save(order1);
        session.save(order2);
        session.save(customer);
    }

    @Test
    public void testComponent () {
        Pay pay = new Pay(1000, 13000, 3000);
        Worker worker = new Worker("AA", pay);
        session.save(worker);
    }

    @Test
    public void testGetBlob () throws SQLException, IOException {
        News news = session.get(News.class, 1);
        System.out.println(news.getImage().getBinaryStream().available());
    }

    @Test
    public void testBlob () throws IOException {
        News news = new News();
        news.setAuthor("AA");
        news.setTitle("aaaa");
        news.setDate(new Date());
        news.setContent("Content");
        InputStream stream = new FileInputStream("C:\\Users\\XT\\Pictures\\oasis_1080.jpg");
        Blob image = Hibernate.getLobCreator(session).createBlob(stream, stream.available());
        news.setImage(image);

        session.save(news);
    }

    @Test
    public void testFormular () {
        News news = session.get(News.class, 1);
        System.out.println(news.getDesc());
    }

    @Test
    public void testPropertyUpdate(){
        News news = session.get(News.class, 1);
        news.setTitle("aaaa");

        System.out.println(news.getDesc());
        System.out.println(news.getDate().getClass());
    }

    @Test
    public void testIdGenerator() throws InterruptedException{
        News news = new News("AA", "aa", new Date());
        session.save(news);

//		Thread.sleep(5000);
    }

    @Test
    public void testDoWork () {
        session.doWork(new Work() {
            @Override
            public void execute(Connection connection) throws SQLException {
                System.out.println(connection);

                //调用存储过程.
            }
        });
    }

    /**
     * evict: 从 session 缓存中把指定的持久化对象移除
     */
    @Test
    public void testEvict(){
        News news1 = session.get(News.class, 1);
        News news2 = session.get(News.class, 2);

        news1.setTitle("AA");
        news2.setTitle("BB");

        session.evict(news1);
    }

    /**
     * delete: 执行删除操作. 只要 OID 和数据表中一条记录对应, 就会准备执行 delete 操作
     * 若 OID 在数据表中没有对应的记录, 则抛出异常
     *
     * 可以通过设置 hibernate 配置文件 hibernate.use_identifier_rollback 为 true,
     * 使删除对象后, 把其 OID 置为  null
     */
    @Test
    public void testDelete(){
//		News news = new News();
//		news.setId(300);

        News news = session.get(News.class, 201);
        session.delete(news);

        System.out.println(news);
    }

    /**
     * 注意:
     * 1. 若 OID 不为 null, 但数据表中还没有和其对应的记录. 会抛出一个异常.
     * 2. 了解: OID 值等于 id 的 unsaved-value 属性值的对象, 也被认为是一个游离对象
     */
    @Test
    public void testSaveOrUpdate(){
        News news = new News("FFF", "fff", new Date());
        news.setId(11);

        session.saveOrUpdate(news);
    }

    /**
     * update:
     * 1. 若更新一个持久化对象, 不需要显示的调用 update 方法. 因为在调用 Transaction
     * 的 commit() 方法时, 会先执行 session 的 flush 方法.
     * 2. 更新一个游离对象, 需要显式的调用 session 的 update 方法. 可以把一个游离对象
     * 变为持久化对象
     *
     * 需要注意的:
     * 1. 无论要更新的游离对象和数据表的记录是否一致, 都会发送 UPDATE 语句.
     *    如何能让 updat 方法不再盲目的出发 update 语句呢 ? 在 .hbm.xml 文件的 class 节点设置
     *    select-before-update=true (默认为 false). 但通常不需要设置该属性.
     *
     * 2. 若数据表中没有对应的记录, 但还调用了 update 方法, 会抛出异常
     *
     * 3. 当 update() 方法关联一个游离对象时,
     * 如果在 Session 的缓存中已经存在相同 OID 的持久化对象, 会抛出异常. 因为在 Session 缓存中
     * 不能有两个 OID 相同的对象!
     *
     */
    @Test
    public void testUpdate(){
        News news = session.get(News.class, 1);

        transaction.commit();
        session.close();

//		news.setId(100);

        session = sessionFactory.openSession();
        transaction = session.beginTransaction();

        news.setAuthor("Oracle");
//        session.update(news);
    }

    /**
     * get VS load:
     *
     * 1. 执行 get 方法: 会立即加载对象.
     *    执行 load 方法, 若不使用该对象, 则不会立即执行查询操作, 而返回一个代理对象
     *
     *    get 是 立即检索, load 是延迟检索.
     *
     * 2. load 方法可能会抛出 LazyInitializationException 异常:
     * 在需要初始化代理对象之前已经关闭了 Session
     *
     * 3. 若数据表中没有对应的记录, Session 也没有被关闭.
     *    get 返回 null
     *    load 若不使用该对象的任何属性, 没问题; 若需要初始化了, 抛出异常.
     */
    @Test
    public void testLoad(){

        News news = (News) session.load(News.class, 10);
        System.out.println(news.getClass().getName());

//		session.close();
//		System.out.println(news);
    }

    @Test
    public void testGet(){
        News news = (News) session.get(News.class, 1);
//		session.close();
        System.out.println(news);
    }

    /**
     * persist(): 也会执行 INSERT 操作
     *
     * 和 save() 的区别 :
     * 在调用 persist 方法之前, 若对象已经有 id 了, 则不会执行 INSERT, 而抛出异常
     */
    @Test
    public void testPersist(){
        News news = new News();
        news.setTitle("EE");
        news.setAuthor("ee");
        news.setDate(new Date());
        news.setId(201);

        session.persist(news);
    }

    /**
     * 1. save() 方法
     * 1). 使一个临时对象变为持久化对象
     * 2). 为对象分配 ID.
     * 3). 在 flush 缓存时会发送一条 INSERT 语句.
     * 4). 在 save 方法之前的 id 是无效的
     * 5). 持久化对象的 ID 是不能被修改的!
     */
    @Test
    public void testSave () {
        News news = new News("Android", "Google", new Date());
        news.setId(101);
        System.out.println(news);

        session.save(news);
        System.out.println(news);
//        news.setId(101);

    }

    /**
     * clear(): 清理缓存
     */
    @Test
    public void testClear () {
        News news = session.get(News.class, 1);
        System.out.println(news);

        session.clear();
        News news2 = session.get(News.class, 1);
        System.out.println(news2);
    }

    /**
     * refresh(): 会强制发送 SELECT 语句, 以使 Session 缓存中对象的状态和数据表中对应的记录保持一致!
     */
    @Test
    public void testRefresh () {
        News news = session.get(News.class, 1);
        System.out.println(news);

        session.refresh(news);
        System.out.println(news);
    }

    /**
     * flush: 使数据表中的记录和 Session 缓存中的对象的状态保持一致. 为了保持一致, 则可能会发送对应的 SQL 语句.
     * 1. 在 Transaction 的 commit() 方法中: 先调用 session 的 flush 方法, 再提交事务
     * 2. flush() 方法会可能会发送 SQL 语句, 但不会提交事务.
     * 3. 注意: 在未提交事务或显式的调用 session.flush() 方法之前, 也有可能会进行 flush() 操作.
     * 1). 执行 HQL 或 QBC 查询, 会先进行 flush() 操作, 以得到数据表的最新的记录
     * 2). 若记录的 ID 是由底层数据库使用自增的方式生成的, 则在调用 save() 方法时, 就会立即发送 INSERT 语句.
     * 因为 save 方法后, 必须保证对象的 ID 是存在的!
     */
    @Test
    public void testSessionFlush () {
        News news = session.get(News.class, 1);
        news.setAuthor("Oracle");

//        session.flush();

        News news1 = new News("Scala", "Google", new Date());
        session.save(news1);
    }

    // Session 一级缓存
    @Test
    public void testSessionCache () {

        News news = session.get(News.class, 1);
        System.out.println(news);

        News news2 = session.get(News.class, 1);
        System.out.println(news2);

    }
}
