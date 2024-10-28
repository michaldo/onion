package onion1jdbc.application;

import onion1jdbc.domain.Notification;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository {

    void insertAll(List<Notification> notifications);
    void insert(Notification notification);
}
