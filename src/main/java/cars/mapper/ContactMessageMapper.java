package cars.mapper;

import cars.domain.ContactMessage;
import cars.dto.ContactMessageDTO;
import cars.dto.request.ContactMessageRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ContactMessageMapper {

    //so here we are getting contactmessagerequest and turning it into contactmessage.
    @Mapping(target = "id",ignore = true)//this means in the target which is contactmessage for us, please ignore id.
    ContactMessage contactMessageRequestToContactMessage(ContactMessageRequest contactMessageRequest);
    /*
    Above we are telling @Mapper to map the request from the client using the entity contactmessage to map the json format into the database
    using java object.
Mapping between DTOs and entities is typically done using Mapper classes. These Mapper classes convert
 data from DTOs to entities and vice versa, ensuring that the right data is sent to the database and returned to the client.
     */

    //here we are converting the contact message to contactMessageDTO

List<ContactMessageDTO>map(List<ContactMessage>contactMessageList);


ContactMessageDTO contactMessageToDTO(ContactMessage contactMessage);

}
