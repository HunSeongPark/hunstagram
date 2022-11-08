package com.example.hunstagram.domain.comment.service;

import com.example.hunstagram.domain.comment.entity.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : Hunseong-Park
 * @date : 2022-11-08
 */
@RequiredArgsConstructor
@Transactional
@Service
public class CommentService {

    private final CommentRepository commentRepository;
}
