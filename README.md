# TradeMate

Java & Spring Boot로 구현한 중고 거래 플랫폼 백엔드 서버입니다.  
사용자 인증, 상품 관리, 거래 상태 관리 기능을 구현하고 AWS EC2 환경에 직접 배포하여 운영한 프로젝트입니다.

---

# Project Goal

TradeMate는 중고 거래 서비스에서 발생하는

- 사용자 권한 관리
- 상품 상태 관리
- 거래 처리 과정
- 동시성 문제

등을 고려하여 실제 서비스 환경을 가정하고 설계한 백엔드 프로젝트입니다.

---

# Tech Stack

Backend

- Java 17
- Spring Boot
- Spring Data JPA (Hibernate)
- JWT Authentication

Database

- MySQL

Infrastructure

- AWS EC2 (Ubuntu 22.04)
- Nginx (Reverse Proxy)
- systemd 서비스 관리

Tools

- Git / GitHub
- IntelliJ

---

## System Architecture

Browser
   │
   ▼
Nginx (80)
   │
   ▼
Spring Boot (8080)
   │
   ▼
MySQL 

Nginx를 Reverse Proxy로 구성하여 외부 요청을 Spring Boot 서버로 전달하도록 설계했습니다.

---

# Key Features

## 회원 시스템

- 회원가입
- 로그인 (JWT 인증)
- 사용자 권한 기반 API 접근

## 상품 관리

- 상품 등록
- 상품 수정
- 상품 삭제
- 상품 조회
- 상품 검색 및 페이징

## 거래 시스템

- 상품 구매 요청
- 거래 완료 처리
- 거래 취소 처리

상품 상태 관리


SELLING → RESERVED → SOLD


---

# Database Design

테이블 구성

- Users
- Items
- Trades

관계

- User 1 : N Item
- Item 1 : 1 Trade

거래 상태 변경은 트랜잭션으로 처리하도록 구현했습니다.

---

# API Example

상품 목록 조회


GET /api/items?page=0&size=10


Response


{
"content": [],
"pageable": {
"pageNumber": 0,
"pageSize": 10
},
"totalPages": 0,
"totalElements": 0
}


---

# Deployment

서버는 AWS EC2 환경에 배포되었습니다.

환경

- Ubuntu 22.04
- OpenJDK 17
- Spring Boot Application
- Nginx Reverse Proxy
- systemd 서비스 관리

접속 주소


http://3.39.248.28/api/items


---

# Troubleshooting

## Public Key Retrieval is not allowed

MySQL 8 연결 시 발생한 문제

해결 방법

JDBC URL 옵션 추가


allowPublicKeyRetrieval=true


---

## EC2 Gradle Build 멈춤 문제

EC2 t3.micro 환경에서 메모리 부족으로 빌드가 멈춤

해결 방법

Swap Memory 생성


sudo fallocate -l 2G /swapfile
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile


---

# What I Learned

- JWT 기반 사용자 인증 구현
- JPA를 활용한 도메인 중심 설계
- 트랜잭션을 고려한 거래 상태 처리
- EC2 기반 서버 배포 및 운영
- Nginx Reverse Proxy 구성
