package com.learning.jpa.onetomany.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.learning.jpa.onetomany.exception.ResourceNotFoundException;
import com.learning.jpa.onetomany.model.Comment;
import com.learning.jpa.onetomany.repository.CommentRepository;
import com.learning.jpa.onetomany.repository.TutorialRepository;

@RestController
@RequestMapping("/api")
public class CommentController {
    @Autowired
    private TutorialRepository tutorialRepository;

    @Autowired
    private CommentRepository commentRepository;

    @GetMapping("/tutorials/{tutorialId}/comments")
    public ResponseEntity<List<Comment>> getAllCommentsByTutorialId(
            @PathVariable(value = "tutorialId") Long tutorialId) {
        if (!tutorialRepository.existsById(tutorialId)) {
            throw new ResourceNotFoundException("Could not find tutorial with id: " + tutorialId);
        }

        final List<Comment> comments = commentRepository.findByTutorialId(tutorialId);

        return new ResponseEntity<List<Comment>>(comments, HttpStatus.OK);
    }

    @GetMapping("/comments/{id}")
    public ResponseEntity<Comment> getCommentById(@PathVariable Long id) {
        final Comment comment = commentRepository.findById(null)
                .orElseThrow(() -> new ResourceNotFoundException("Could not find comment with id: " + id));

        return new ResponseEntity<Comment>(comment, HttpStatus.OK);
    }

    @PostMapping("/tutorials/{id}/comments")
    public ResponseEntity<Comment> createComment(@PathVariable Long id, @RequestBody Comment commentRequest) {
        final Comment comment = tutorialRepository.findById(id).map(tutorial -> {
            commentRequest.setTutorial(tutorial);
            return commentRepository.save(commentRequest);
        }).orElseThrow(() -> new ResourceNotFoundException("Could not find tutorial with id: " + id));

        return new ResponseEntity<Comment>(comment, HttpStatus.CREATED);
    }

    @PutMapping("/comments/{id}")
    public ResponseEntity<Comment> updateComment(@PathVariable Long id, @RequestBody Comment commentRequest) {
        final Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Could not find comment with id: " + id));

        comment.setContent(commentRequest.getContent());

        return new ResponseEntity<Comment>(commentRepository.save(comment), HttpStatus.OK);
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<HttpStatus> deleteComment(@PathVariable Long id) {
        commentRepository.deleteById(id);

        return new ResponseEntity<HttpStatus>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/tutorial/{id}/comments")
    public ResponseEntity<HttpStatus> deleteAllCommentsByTutorialId(@PathVariable Long id) {
        if (!tutorialRepository.existsById(id)) {
            throw new ResourceNotFoundException("Could not find tutorial with id: " + id);
        }

        commentRepository.deleteByTutorialId(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
