package org.swd392.careers.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "careers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Career {
    @Id
    private String id;

    private String name; //major of the university
    private String description;
}