package com.example.hunstagram.domain.postimage.service;

import com.example.hunstagram.domain.postimage.entity.PostImageRepository;
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
public class PostImageService {

    private final PostImageRepository postImageRepository;
}
