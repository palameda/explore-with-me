package ru.practicum.service.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.exception.DeleteEntityException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Category;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.utility.mapper.CategoryMapper;
import ru.practicum.utility.page.Page;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    public CategoryDto create(NewCategoryDto createDto) {
        Category category = Category.builder()
                .name(createDto.getName())
                .build();
        categoryRepository.save(category);
        log.info("Категория {} успешно сохранена", category.toString());
        return categoryMapper.toDto(category);
    }

    @Override
    public void delete(Long categoryId) {
        Optional.of(categoryId)
                .map(id -> categoryRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Категория с id = " + id + " не найдена в системе")))
                .map(eventRepository::findAllByCategory)
                .filter(List::isEmpty)
                .orElseThrow(() -> new DeleteEntityException("Есть связанные события с категорией с id = " + categoryId));
        categoryRepository.deleteById(categoryId);
        log.info("Категория с id = {} успешно удалена", categoryId);
    }

    @Override
    public Collection<CategoryDto> getAll(Integer from, Integer size) {
        List<Category> categories = categoryRepository.findAll(new Page(from, size, Sort.unsorted()))
                .getContent();
        log.info("Получение списка всех категорий размером {}", categories.size());
        return categoryMapper.toDto(categories);
    }

    @Override
    public CategoryDto get(Long id) {
        log.info("Получение категории по id = {}", id);
        return categoryRepository.findById(id)
                .map(categoryMapper::toDto)
                .orElseThrow(() -> new NotFoundException("Категория с id = " + id + " не найдена в системе"));
    }

    @Override
    public CategoryDto update(Long id, NewCategoryDto updateCategory) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Категория с id = " + id + " не найдена в системе"));
        updateName(category, updateCategory.getName());
        return categoryMapper.toDto(categoryRepository.save(category));
    }

    private void updateName(Category category, @Nullable String newCategoryName) {
        Optional.ofNullable(newCategoryName)
                .filter(name -> !name.isBlank())
                .ifPresent(category::setName);
    }
}
