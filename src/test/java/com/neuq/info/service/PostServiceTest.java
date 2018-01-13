package com.neuq.info.service;

import com.neuq.info.dao.PostDao;
import com.neuq.info.dto.ResultModel;
import com.neuq.info.entity.Post;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.*;

/**
 * Created by lihang on 2017/4/4.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration({"classpath:spring/spring-service.xml",
        "classpath:spring/spring-dao.xml", "classpath:spring/spring-web.xml"})
public class PostServiceTest {
    @Autowired
    private PostService postService;
    @Autowired
    private PostDao postDao;

    @Test
    public void insertPost() throws Exception {
        Post post = new Post();
        post.setContent("我爱你");
        post.setTitle("我也爱你");
        post.setSecret(1);
        post.setUserId(1000L);
        ResultModel resultModel = postService.insertPost("11", "11", 1, 100l);
        System.out.println(resultModel);
    }

    @Test
    public void queryPostByCount() throws Exception {
        ResultModel resultModel = postService.queryPostByCount(0, 10, 1000l);
//        ResultModel resultModel1=postService.queryPostByCount(36,10,1000l);
//        System.out.println(resultModel);
        System.out.println(resultModel);
    }

    @Test
    public void queryPostByPage() throws Exception {
        ResultModel resultModel = postService.queryPostByPage(1);
        System.out.println(resultModel);
    }

    @Test
    public void queryAllPostCount() throws Exception {

    }

    @Test
    public void deletePost() throws Exception {
        postService.deletePost(4,1000);
    }

    @Test
    public void updateLike() throws Exception {
        System.out.println(postService.updateLike(4, 1, 1000l));
        System.out.println(postService.updateLike(4, 0, 1000l));
    }

    @Test
    public void queryPostByUserId() throws Exception {
        System.out.println(postService.queryPostByUserId(1024l));
    }

    @Test
    public void queryPostByPostId() throws Exception {
        System.out.println(postService.queryPostByPostId(81l, 1024l));
    }
    @Test
    public void queryLikeByUserId() throws Exception {
        System.out.println(postService.queryLikeByUserId(1024l));
    }


}