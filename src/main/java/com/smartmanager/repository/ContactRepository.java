package com.smartmanager.repository;

import com.smartmanager.models.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ContactRepository extends JpaRepository<Contact, Integer> {
//    pagination.....

    //    currentPage
//    contact per page
    @Query("from Contact as c where c.user.uid =:userId")
    Page<Contact> findContactByUser(@Param("userId") int userId, Pageable pageable);


}
