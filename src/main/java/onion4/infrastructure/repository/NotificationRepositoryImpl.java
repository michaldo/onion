package onion4.infrastructure.repository;


import onion4.application.NotificationRepository;
import onion4.domain.Notification;
import onion4.infrastructure.repository.entities.HibernateNotification;
import onion4.infrastructure.repository.hibernaterepo.HibernateNotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class NotificationRepositoryImpl  implements NotificationRepository {

    @Autowired private HibernateNotificationRepository hibernateRepository;

    @Override
    public void insertAll(List<Notification> notifications) {

        List<HibernateNotification> hibernateNotifications = notifications
                .stream()
                .map(n -> new HibernateNotification(n.courseId(), n.studentId(), n.status()))
                .toList();
        hibernateRepository.saveAll(hibernateNotifications);
    }

    @Override
    public void insert(Notification notification) {
        hibernateRepository.save(new HibernateNotification(notification.courseId(), notification.studentId(), notification.status()));

    }
}
