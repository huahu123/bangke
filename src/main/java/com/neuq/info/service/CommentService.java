package com.neuq.info.service;

import com.neuq.info.dto.ResultModel;

/**
 * Created by lihang on 2017/4/21.
 */
public interface CommentService {
    ResultModel queryComment(long postid,long userId);
    ResultModel addComment(String content,long fromUserId,long postid,int level,long pCommentId,long toUserId);
    ResultModel delComment(long commentId,long userId);

}
