package ru.practicum.utility.mapper;

import org.mapstruct.Mapper;
import ru.practicum.api.category.CategoryDto;
import ru.practicum.model.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper extends DefaultMapper<Category, CategoryDto> {
}
