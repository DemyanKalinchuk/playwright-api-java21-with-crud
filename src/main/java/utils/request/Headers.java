package utils.request;

import java.util.ArrayList;
import java.util.List;

public class Headers {

    List<Header> headers;

    public Headers addHeader(String name, String value) {
        headers.add(new Header(name, value));
        return this;
    }

    public Headers addAuthHeader(String value) {
        headers.add(new Header("Authorization", value));
        return this;
    }

    public Headers() {
        headers = new ArrayList<>();
    }

    public int getSize() {
        return headers.size();
    }

    public boolean isContainHeader(String headerName) {
        return headers.contains(headerName);
    }

    public String[] getHeader() {
        if (headers.size() > 0) {
            Header header = headers.get(0);
            String[] array = {header.getName(), header.getValue()};
            headers.remove(0);
            return array;
        }
        return null;
    }
}

class Header {
    String name;
    String value;

    public Header(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
