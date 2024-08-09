# Member 회원 기능

## Overview
이메일 및 닉네임 중복 체크, 회원가입, 회원 정보 조회, 비밀번호 변경, 회원 탈퇴 등의 기능을 포함합니다.

## Structure
- MemberController: 회원 관련 API 엔드포인트 제공
- MemberService: 회원 관련 비즈니스 로직 처리
- MemberRepository: JPA를 사용하여 회원 정보를 데이터베이스에 저장 및 조회
- BCryptPasswordEncoder: 비밀번호 암호화를 위한 Spring Security 클래스

## Features
1. <a href="https://github.com/LuckyVickys/woosan-back/blob/main/Readme.assets/SignUp.md">**이메일 중복 체크**</a>: 주어진 이메일이 이미 등록되어 있는지 확인합니다.
2. <a href="https://github.com/LuckyVickys/woosan-back/blob/main/Readme.assets/SignUp.md">**닉네임 중복 체크**</a>: 주어진 닉네임이 이미 등록되어 있는지 확인합니다.
3. <a href="https://github.com/LuckyVickys/woosan-back/blob/main/Readme.assets/SignUp.md">**회원가입**</a>: 새로운 회원을 등록하고, 가입 성공 시 회원 정보를 반환합니다.
4. <a href="https://github.com/LuckyVickys/woosan-back/blob/main/Readme.assets/SignUp.md">**회원가입 코드 메일 전송**</a>: 회원가입 확인 코드를 이메일로 전송합니다.
5. <a href="https://github.com/LuckyVickys/woosan-back/blob/main/Readme.assets/SignUp.md">**회원가입 코드 체크**</a>: 발송된 회원가입 확인 코드가 유효한지 확인합니다.
6. **임시 비밀번호 메일 전송**: 임시 비밀번호를 생성하여 이메일로 전송하고, 회원 비밀번호를 임시 비밀번호로 변경합니다.
7. **비밀번호 변경**: 현재 비밀번호를 확인한 후 새로운 비밀번호로 변경합니다.
8. **로그인 한 멤버 정보 조회**: 로그인된 회원의 정보를 조회합니다.
9. **회원 탈퇴**: 회원 탈퇴 처리를 합니다.
