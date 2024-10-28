package onion4.application;

import onion4.domain.Notification;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository {

    void insertAll(List<Notification> notifications);
    void insert(Notification notification);
}
