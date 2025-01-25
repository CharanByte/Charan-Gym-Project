package com.xworkz.gym.service;

import com.xworkz.gym.dto.AdminLoginDTO;
import com.xworkz.gym.dto.EnquiryDTO;
import com.xworkz.gym.dto.RegistrationDTO;
import com.xworkz.gym.entity.*;
import com.xworkz.gym.repository.GymRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

@Service
public class GymServiceImp implements GymService{

    @Autowired
    private GymRepository gymRepository;
    private  final BCryptPasswordEncoder bCryptPasswordEncoder=new BCryptPasswordEncoder();



    @Override
    public boolean validateAdminUser(AdminLoginDTO adminLoginDTO) {
      AdminEntity adminEntity=gymRepository.validateUser(adminLoginDTO);
        System.out.println(adminEntity);
        if(adminEntity!=null && bCryptPasswordEncoder.matches(adminLoginDTO.getPassword(),adminEntity.getPassword())){
            return  true;
        }
        return false;
    }
    @Override
    public Long getCountOfAdminUserName(String email) {
       long count= gymRepository.getCountOfAdminUserName(email);
        return count;
    }

    @Override
    public void validAndSaveNewPassword(AdminLoginDTO adminLoginDTO) {
        if(adminLoginDTO.getPassword()!=null){
            String encodedPassword=bCryptPasswordEncoder.encode(adminLoginDTO.getPassword());
            AdminEntity adminEntity=gymRepository.validateUser(adminLoginDTO);
            adminEntity.setPassword(encodedPassword);
            adminEntity.setLogin_count(0);
            gymRepository.updateAdminPasswordAndCount(adminEntity);
        }
    }


    @Override
    public AdminEntity getAdminDetails(AdminLoginDTO adminLoginDTO) {

        return gymRepository.validateUser(adminLoginDTO);
    }



    @Override
    public boolean validateCustomerEnquiryDetails(EnquiryDTO enquiryDTO,String createdName) {
        boolean valid=true;
        String name=enquiryDTO.getName();
        if(name!=null && name.length()>1 && name.length()<30){
            System.out.println("name is valid");
        }
        else {
            valid=false;
            System.out.println("name is in valid");
        }
        String email=enquiryDTO.getEmail();
        if(email!=null && (email.contains("@gmail.com")||email.contains(".in"))){
            System.out.println("email valid");
        }
        else {
            valid=false;
            System.out.println("email Invalid");
        }
        String phoneNo= String.valueOf(enquiryDTO.getPhoneNumber());
        if(phoneNo!=null && phoneNo.length()==10){
            System.out.println("phoneNo valid");
        }
        else {
            valid=false;
            System.out.println("phoneNo Invalid");
        }
        int age=enquiryDTO.getAge();
        if(age>=12){
            System.out.println("age valid");
        }
        else {
            valid=false;
            System.out.println("age Invalid");
        }

        if(valid){
        EnquiryEntity enquiryEntity=new EnquiryEntity();
        enquiryEntity.setName(enquiryDTO.getName());
        enquiryEntity.setEmail(enquiryDTO.getEmail());
        enquiryEntity.setPhoneNumber(enquiryDTO.getPhoneNumber());
        enquiryEntity.setAge(enquiryDTO.getAge());
        enquiryEntity.setGender(enquiryDTO.getGender());
        enquiryEntity.setAddress(enquiryDTO.getAddress());
        enquiryEntity.setStatus("Enquiry");
        enquiryEntity.setAreaName(enquiryDTO.getAreaName());
        enquiryEntity.setCreatedBy(createdName);

        gymRepository.saveCustomerEnquiryDetails(enquiryEntity);


            final String username = "fittnessgym6@gmail.com";
            final String userPassword = "dfxi ijpe mliy povj";

            Properties prop = new Properties();
            prop.put("mail.smtp.host", "smtp.gmail.com");
            prop.put("mail.smtp.port", "587");
            prop.put("mail.smtp.auth", "true");
            prop.put("mail.smtp.starttls.enable", "true"); //TLS

            Session session = Session.getInstance(prop,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(username, userPassword);
                        }
                    });
            try {
                String emailContent = String.format(
                        "Dear %s,\n\n" +
                                "Thank you for reaching out to us! We have successfully received your enquiry with the following details:\n\n" +
                                "Name: %s\n" +
                                "Email: %s\n" +
                                "Phone Number: %s\n" +
                                "Age: %d\n" +
                                "Gender: %s\n" +
                                "Address: %s\n" +
                                "Area Name: %s\n" +
                                "Status: %s\n\n" +
                                "Our team will review your enquiry and get back to you shortly. If you have any additional questions or require further assistance, feel free to contact us.\n\n" +
                                "Best regards,\n" +
                                "%s",
                        enquiryEntity.getName(),
                        enquiryEntity.getName(),
                        enquiryEntity.getEmail(),
                        enquiryEntity.getPhoneNumber(),
                        enquiryEntity.getAge(),
                        enquiryEntity.getGender(),
                        enquiryEntity.getAddress(),
                        enquiryEntity.getAreaName(),
                        enquiryEntity.getStatus(),
                        "Fittnes Gym"
                );


                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(username));
                message.setRecipients(
                        Message.RecipientType.TO,
                        InternetAddress.parse(enquiryEntity.getEmail())
                );
                message.setSubject("Enquiry Submission Confirmation");
                message.setText(emailContent);

                Transport.send(message);

                System.out.println("Email sent successfully!");

            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
        return valid;
    }

    @Override
    public List<EnquiryEntity> getAllEnquiryUsersDetails() {
        List<EnquiryEntity> enquiryEntity= gymRepository.getAllEnquiryUsersDetails();
        return enquiryEntity;
    }

    @Override
    public int updateUserEnquiryDetails(int enquiryId, String status, String reason,String adminName) {

        LocalDateTime  localDateTime=LocalDateTime.now();
        int updatedValue=gymRepository.updateUserEnquiryDetails(enquiryId,status,reason,adminName,localDateTime);
        UpdatedEnquiryDetailsEntity updatedEnquiryDetails=new UpdatedEnquiryDetailsEntity();
        updatedEnquiryDetails.setId(enquiryId);
        updatedEnquiryDetails.setCustomer_status(status);
        updatedEnquiryDetails.setCustomer_reason(reason);
        updatedEnquiryDetails.setUpdatedBy(adminName);
        updatedEnquiryDetails.setUpdatedDate(localDateTime);
        gymRepository.saveUserUpdatedEnquiryDetails(updatedEnquiryDetails);
        return updatedValue;
    }

    @Override
    public List<EnquiryEntity> getAllUserDetailsByStatus(String status) {

        return gymRepository.getAllUserDetailsByStatus(status);
    }

    @Override
    public boolean validateAndSaveRegistredDetails(RegistrationDTO registrationDTO,String adminName) {
        boolean valid=true;
        String name=registrationDTO.getName();
        if(name!=null && name.length()>1 && name.length()<30 &&  name.matches("^[A-Za-z]+(?: [A-Za-z]+)*$")){
            System.out.println("name is valid");
        }
        else {
            valid=false;
            System.out.println("name is in valid");
        }
        String email=registrationDTO.getEmail();
        if(email!=null && email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")){
            System.out.println("email valid");
        }
        else {
            valid=false;
            System.out.println("email Invalid");
        }
        String phoneNo= String.valueOf(registrationDTO.getPhoneNo());
        if(phoneNo!=null && phoneNo.matches("^[0-9]{10}$")){
            System.out.println("phoneNo valid");
        }
        else {
            valid=false;
            System.out.println("phoneNo Invalid");
        }
        String password = registrationDTO.getPassword();
        if (password != null && password.matches("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$")) {
            System.out.println("Password is valid");
        } else {
            valid = false;
            System.out.println("Password is invalid");
        }



        if(valid) {

            RegistrationEntity registrationEntity = new RegistrationEntity();
            registrationEntity.setName(registrationDTO.getName());
            registrationEntity.setEmail(registrationDTO.getEmail());
            registrationEntity.setPassword(registrationDTO.getPassword());
            registrationEntity.setPhoneNumber(registrationDTO.getPhoneNo());
            registrationEntity.setGympackage(registrationDTO.getGympackage());
            registrationEntity.setTrainer(registrationDTO.getTrainer());
            registrationEntity.setDiscount(registrationDTO.getDiscount());
            registrationEntity.setAmount(registrationDTO.getAmount());
            registrationEntity.setAmountPaid(registrationDTO.getAmountPaid());
            registrationEntity.setBalanceAmount(registrationDTO.getBalanceAmount());
            registrationEntity.setTotalAmountPaid(registrationDTO.getAmountPaid());
            registrationEntity.setCreatedBy(adminName);
            gymRepository.saveRegistredDetails(registrationEntity);


            final String username = "fittnessgym6@gmail.com";
            final String userPassword = "dfxi ijpe mliy povj";

            Properties prop = new Properties();
            prop.put("mail.smtp.host", "smtp.gmail.com");
            prop.put("mail.smtp.port", "587");
            prop.put("mail.smtp.auth", "true");
            prop.put("mail.smtp.starttls.enable", "true"); //TLS

            Session session = Session.getInstance(prop,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(username, userPassword);
                        }
                    });
            try {
                String emailContent = String.format(
                        "Subject: Registration Confirmation\n\n" +
                                "Dear %s,\n\n" +
                                "Thank you for registering with us! Here are your registration details:\n\n" +
                                "Name: %s\n" +
                                "Email: %s\n" +
                                "Password: %s\n" +
                                "Phone Number: %s\n" +
                                "Gym Package: %s\n" +
                                "Trainer Name: %s\n" +
                                "Discount: %s%%\n" +
                                "Total Amount: %.2f\n" +
                                "Amount Paid: %.2f\n" +
                                "Balance Amount: %.2f\n" +
                                "Total Amount Paid: %.2f\n\n" +
                                "We recommend keeping your password secure and not sharing it with anyone.\n\n" +
                                "We’re excited to have you on board and look forward to helping you achieve your fitness goals. If you have any questions or need further assistance, please feel free to contact us.\n\n" +
                                "Best regards,\n" +
                                "Fittness Gym",
                        registrationEntity.getName(),
                        registrationEntity.getName(),
                        registrationEntity.getEmail(),
                        registrationEntity.getPassword(),
                        registrationEntity.getPhoneNumber(),
                        registrationEntity.getGympackage(),
                        registrationEntity.getTrainer(),
                        registrationEntity.getDiscount(),
                        registrationEntity.getAmount(),
                        registrationEntity.getAmountPaid(),
                        registrationEntity.getBalanceAmount(),
                        registrationEntity.getTotalAmountPaid()

                );


                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(username));
                message.setRecipients(
                        Message.RecipientType.TO,
                        InternetAddress.parse(registrationEntity.getEmail())
                );
                message.setSubject("Registration Confirmation");
                message.setText(emailContent);

                Transport.send(message);

                System.out.println("Registration email sent successfully!");

            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
        return valid;
    }

    @Override
    public List<RegistrationEntity> getAllRegistredUsersDetails() {

        return gymRepository.getAllRegistredUsersDetails();
    }

    @Override
    public int upadteRegistredUsersDetails(int id, String gympackage, String trainer, double amountPaid, double balanceAmount, double totalAmount,String adminName) {

        int value=gymRepository.upadteRegistredUsersDetails(id,gympackage,trainer,amountPaid,balanceAmount,totalAmount);
        UpdatedRegistrationDetailsEntity details=new UpdatedRegistrationDetailsEntity();
        details.setId(id);
        details.setAmount_paid(amountPaid);
        details.setAmount_balance(balanceAmount);
        details.setUpdated_packagel(gympackage);
        details.setUpdated_trainer(trainer);
        details.setUpdated_by(adminName);

        gymRepository.saveUpadteRegistredUsersDetails(details);
        return value;

    }

    @Override
    public List<UpdatedEnquiryDetailsEntity> getAllViewDetails(int id) {

        List<UpdatedEnquiryDetailsEntity> list=gymRepository.getAllViewDetails(id);
        return list;
    }

    @Override
    public List<RegistrationEntity> getAllRegistredUsersDetailsByNameAndPhoneNo(String searchName, Long searchPhoneNo) {


        return gymRepository.getAllRegistredUsersDetailsByNameAndPhoneNo( searchName, searchPhoneNo);
    }

    @Override
    public List<UpdatedRegistrationDetailsEntity> getAllRegistredUsersUpdatedDetails(int id) {
        return gymRepository.getAllRegistredUsersUpdatedDetails(id);
    }

    @Override
    public List<RegistrationEntity> getAllRegistredUserDetailsById(int id) {

        return gymRepository.getAllRegistredUserDetailsById(id);
    }

    @Override
    public Long getCountOfRegisteredUserEmail(String email) {
        return gymRepository.getCountOfRegisteredUserEmail(email);
    }

}
