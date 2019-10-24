package fileExplorer;

import java.io.File;
import java.util.ArrayList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

class MediaFile extends File {

    private static final long serialVersionUID = 3503384315825819169L;

    private String comment;
    private String imagePath;
    private ArrayList<Category> categories;

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public ArrayList<Category> getCategories() {
        return this.categories;
    }

    public void setCategories(ArrayList<Category> categories) {
        this.categories = categories;
    }

    public void addCategory(Category newCategory) {
        this.categories.add(newCategory);
    }

    public void removeCategory(Category newCategory) {
        this.categories.remove(newCategory);
    }

    public MediaFile(String path) {
        super(path);
        this.comment = "";
        this.imagePath = "";
        this.categories = new ArrayList<>();
    }

    public MediaFile(JSONObject mediaFile, String path) {
        super(path);
        this.comment = (String) mediaFile.get("comment");
        this.imagePath = (String) mediaFile.get("imagePath");
        this.categories = new ArrayList<>();

        JSONArray savedCategories = (JSONArray) mediaFile.get("categories");
        for (int i = 0; i < savedCategories.size(); i++) {
            String category = (String) savedCategories.get(i);

            Category cat = CategoryFactory.getCategory(category);
            if (cat == null) {
                cat = CategoryFactory.makeCategory(category);
            }

            this.categories.add(cat);

        }
    }
}
