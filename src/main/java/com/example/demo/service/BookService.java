package com.example.demo.service;

import com.example.demo.dto.BookRequestDto;
import com.example.demo.dto.BookResponseDto;
import com.example.demo.entity.Book;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    public List<BookResponseDto> getAllBooks() {
        return bookRepository.findAll()
                .stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    public BookResponseDto getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Książka o id " + id + " nie istnieje"));
        return toResponseDto(book);
    }

    public BookResponseDto createBook(BookRequestDto dto) {
        if (bookRepository.existsByIsbn(dto.getIsbn())) {
            throw new IllegalArgumentException("Książka z ISBN " + dto.getIsbn() + " już istnieje");
        }
        Book book = toEntity(dto);
        return toResponseDto(bookRepository.save(book));
    }

    public BookResponseDto updateBook(Long id, BookRequestDto dto) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Książka o id " + id + " nie istnieje"));

        bookRepository.findByIsbn(dto.getIsbn())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new IllegalArgumentException("ISBN " + dto.getIsbn() + " jest już zajęty");
                    }
                });

        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setIsbn(dto.getIsbn());
        book.setYear(dto.getYear());
        book.setPrice(dto.getPrice());

        return toResponseDto(bookRepository.save(book));
    }

    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new ResourceNotFoundException("Książka o id " + id + " nie istnieje");
        }
        bookRepository.deleteById(id);
    }

    private BookResponseDto toResponseDto(Book book) {
        return new BookResponseDto(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.getYear(),
                book.getPrice()
        );
    }

    private Book toEntity(BookRequestDto dto) {
        Book book = new Book();
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setIsbn(dto.getIsbn());
        book.setYear(dto.getYear());
        book.setPrice(dto.getPrice());
        return book;
    }
}