import java.util.ArrayList;

public class APIResponse {
    SearchInfo searchInformation;
    ArrayList<Item> items;

    public SearchInfo getSearchInformation() {
        return searchInformation;
    }

    public ArrayList<Item> getItems() {
        return items;
    }
}
