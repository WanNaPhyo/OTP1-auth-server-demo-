package com.example.authserverdemo.service;

import com.example.authserverdemo.dao.OtpRepository;
import com.example.authserverdemo.dao.UserRepository;
import com.example.authserverdemo.entity.Otp;
import com.example.authserverdemo.entity.User;
import com.example.authserverdemo.util.GenerateCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OtpRepository otpRepository;

    public void addUser(User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public boolean check(Otp otpToValidate){
        Optional<Otp> userOtp=
                otpRepository.findOtpByUsername(otpToValidate.getUsername());
        if (userOtp.isPresent()){
            Otp otp=userOtp.get();
            if (otpToValidate.getCode().equals(otp.getCode())){
                return true;
            }
        }
        return false;
    }

    public void auth(User user){
        Optional<User> userOptional=
                userRepository.findUserByUsername(user.getUsername());
        if (userOptional.isPresent()){
            User u=userOptional.get();
            if (passwordEncoder.matches(user.getPassword(),
                    u.getPassword())){
                renewOtp(u);
            }
            else {
                throw new BadCredentialsException("Bad Credentials.");
            }
        }
        else {
            throw new BadCredentialsException("Bad Credentials.");
        }
    }


    private void renewOtp(User u){
        String code= GenerateCodeUtil.generateCode();
        Optional<Otp> userOtp=
                otpRepository.findOtpByUsername(u.getUsername());
        if (userOtp.isPresent()){
            Otp otp=userOtp.get();
            otp.setCode(code);
        }
        else {
            Otp otp=new Otp();
            otp.setUsername(u.getUsername());
            otp.setCode(code);
            otpRepository.save(otp);
        }
    }
}
