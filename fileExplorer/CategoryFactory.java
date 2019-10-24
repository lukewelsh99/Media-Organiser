package fileExplorer;

import java.util.ArrayList;

public class CategoryFactory {

    private static ArrayList<Category> categoryList = new ArrayList<>();

    public static ArrayList<Category> getCategoryList() {
        return categoryList;
    }

    public static Category makeCategory(String categoryName) {
        Category newCategory = new Category(categoryName);
        categoryList.add(newCategory);
        return newCategory;
    }

    public static Category getCategory(String categoryName) {
        Category category = null;
        for (int i = 0; i < categoryList.size(); i++) {
            if (categoryList.get(i).getName().equals(categoryName)) {
                category = categoryList.get(i);
                break;
            }
        }
        return category;
    }

}
