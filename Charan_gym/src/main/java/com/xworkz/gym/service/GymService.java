package com.xworkz.gym.service;

import com.xworkz.gym.dto.AdminLoginDTO;
import com.xworkz.gym.dto.EnquiryDTO;
import com.xworkz.gym.dto.RegistrationDTO;
import com.xworkz.gym.dto.TrainerDTO;
import com.xworkz.gym.entity.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface GymService {

     boolean validateAdminUser(AdminLoginDTO adminLoginDTO);

     boolean validateCustomerEnquiryDetails(EnquiryDTO enquiryDTO,String name);

     AdminEntity getAdminDetails(AdminLoginDTO adminLoginDTO);

     void validAndSaveNewPassword(AdminLoginDTO adminLoginDTO);

    List<EnquiryEntity> getAllEnquiryUsersDetails();

    Long getCountOfAdminUserName(String email);

    int updateUserEnquiryDetails(int enquiryId, String status, String reason,String adminName);

    List<EnquiryEntity> getAllUserDetailsByStatus(String status);

    boolean validateAndSaveRegistredDetails(RegistrationDTO registrationDTO,String AdminName);

    List<RegistrationEntity> getAllRegistredUsersDetails();

    int upadteRegistredUsersDetails(int id, String gympackage, String trainer, double amountPaid, double balanceAmount,double totalAmount,String adminName);

    List<UpdatedEnquiryDetailsEntity> getAllViewDetails(int id);

    List<RegistrationEntity> getAllRegistredUsersDetailsByNameAndPhoneNo(String searchName, Long searchPhoneNo);

    List<UpdatedRegistrationDetailsEntity> getAllRegistredUsersUpdatedDetails(int id);

    List<RegistrationEntity> getAllRegistredUserDetailsById(int id);

    Long getCountOfRegisteredUserEmail(String email);

    int validteUserPasswordNyEmail(String email, String password);

    int validateAndupdateNewPassword( int id,String password, String confirmpassword);

    RegistrationEntity getAllRegistredUsersDetailsByEmail(String useremail);

    RegistrationEntity getAllRegistredUsersDetailsById(int id);

    int updateUserProfile(RegistrationDTO registrationDTO, String filePath,int id);

    Long getCountOfEmail(String email);

    Long getCountOfRegEmail(String email);

    void saveTrainerDetails(TrainerDTO trainerDTO);

    void saveslots(String startTime, String endTime, String duration);

    List<SlotsEntity> getAllSlotsDetails();

    List<TrainerEntity> getAllTrainerDetails();

    int deleteSlotById(int idForDelete);

    void assignUsersToTrainer(String trainerName, List<String> userNames,String slot);

    int deleteTrainerSlot(int trainerId);

    UsersAssignedToTrainerEntity getTrainerAndSlotByUserName(String name);

    List<UsersAssignedToTrainerEntity> getUsersAssignedToTrainerByTrainerName(String trainerName);

    int deleteUserAssignedToTrainer(String trainerName);
}
