package dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Getter
public class RequestDto {
    @Setter
    private String to;
    @Setter
    private String requestStr;
    private List<String> request;

    public RequestDto() {
        this.request = new ArrayList<>();
    }

    public RequestDto(String to, String requestStr) {
        this.to = to;
        this.request = new ArrayList<>();
        this.setRequest(requestStr);
    }


    public void setRequest(String requestStr){
        if(requestStr.contains(" ")) {
            String[] requests = requestStr.split(" ");
            this.request.addAll(Arrays.asList(requests));
        } else {
            this.request.add(requestStr);
        }
    }
}
