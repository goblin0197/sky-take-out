package com.sky.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

//@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private PasswordEncoder pe;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //1.查询数据库判断用户名是否存在，如果不存在则抛出异常UsernameNotFoundException
                //怎么查数据库？这应该都会吧，我这里就不弄查数据库的部分了，节省时间，写几个小时了，这里我们就自己造一个数据吧，
                //假设我们从数据中查到的用户名为YangJunDong，我们直接判断就是
                if(!"YangJunDong".equals(username)) { //注意这个username是传递过来的参数，也就是前端输入框中的名字
                    throw new UsernameNotFoundException("用户名不存在！");
                }
        //2.将查询出来的密码（数据库中已加密的密码）进行解析，或者直接放到实现类的构造函数中？
                /*
                这里咱也不查数据库了，自己造一个密码吧，但是这个密码是数据库中的，我们得造一个加密的密码
                之前写的配置类管用了吧，我们直接Resource注入进来就好了
                 */
                String password = pe.encode("123");
        //通过数据库查到密码之后怎么做？我们这里采用直接放到到实现类(UserDetailsService的最终实现类是啥来着)的构造函数中,代码如下
                return new User(username,password, AuthorityUtils.commaSeparatedStringToAuthorityList( "admin" ));
                //这里的三个参数，前两个用户名密码，不必多说，但是第三个是权限信息，同样的我们没查数据库，也只能造一个，使用AuthorityUtils工具类中
               //的commaSeparatedStringToAuthorityList方法，给其设置权限，我设置的权限是admin的权限，当然也可以短号隔开设置多个权限，一个人也存有多种权限的情况的
    }
}