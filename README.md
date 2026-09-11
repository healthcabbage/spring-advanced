🌙 **붉은 달의 성채 - 게임 저장 서버 구현**

붉은 달의 성채의 게임 진행 데이터를 저장하고, 관리하기 위한 Spring Boot 기반 게임 저장 서버입니다.

게임의 생성, 조회, 진행 상태 저장, 삭제 기능, 이름 변경을 API로 구현했으며, 게임 진행 정부와 게임에 사용되는 RunCard(덱) 데이터를 함께 관리합니다.

📌 **프로젝트 개요**

게임 플레이 중 발생하는 진행 데이터를 서버에 저장하여 게임 상태를 관리할 수 있도록 구현했습니다.

저장되는 주요 게임 정보는 다음과 같습니다.

플레이어 이름
현재 체력
현재 층
게임 Phase
게임 Status
현재 덱(RunCard)

특히 이미 종료된 게임에 대해서는 진행 저장 요청을 차단하여 기존 게임 데이터가 변경되지 않도록 예외 처리를 적용했습니다.

🛠 **기술 스택**

Java 21
Spring Boot
Spring Data JPA
MySQL
Gradle
Docker
Postman

✨ **주요 기능**

🎮**게임 관리**

게임 생성
게임 목록 조회
게임 상세 조회
플레이어 이름 변경
게임 삭제

💾 **게임 진행 저장**

게임 진행 중 변경되는 데이터를 서버에 저장합니다.

현재 HP
현재 Floor
게임 Phase
게임 Status
RunCard 덱

진행 저장 요청 시 기존 덱을 삭제한 후 요청받은 덱 전체를 다시 저장하도록 구현했습니다.

🔒 **게임 종료 상태 검증**

게임의 isFinished()를 이용하여 게임 종료 여부를 확인합니다.

이미 종료된 게임에 진행 저장 요청이 들어오면 GameFinishedException을 발생시키고, GlobalExceptionHandler에서 409 Conflict로 응답합니다.

이를 통해 종료된 게임의 진행 데이터가 변경되는 것을 방지합니다.

🏗️ **프로젝트 구조**

Controller
    ↓
Service
    ↓
Repository
    ↓
MySQL

**Controller**

HTTP 요청과 응답을 처리합니다.

**Service**

게임 진행 저장 및 삭제 등 게임 저장 서버의 비즈니스 로직을 처리합니다.

**Repository**

Spring Data JPA를 이용하여 Game과 RunCard 데이터를 데이터베이스와 연동합니다.

🧩 **3-Layer Architecture**

Controller, Service, Repository의 역할을 분리하여 구현했습니다.

Client
  │
  ▼
Controller
HTTP 요청/응답 처리
  │
  ▼
Service
비즈니스 로직 처리
  │
  ▼
Repository
데이터베이스 접근
  │
  ▼
MySQL

계층별 역할을 분리하여 코드의 책임을 명확하게 하고 유지보수하기 쉽도록 구성했습니다.

🗃️ **데이터 관계**

하나의 Game은 여러 개의 RunCard를 가질 수 있습니다.

Game
 │
 ├── RunCard
 ├── RunCard
 └── RunCard

게임 삭제 시 해당 게임에 연결된 RunCard도 함께 삭제하도록 구현했습니다.

⚠️ **예외 처리**

GlobalExceptionHandler를 이용하여 예외를 공통적으로 처리합니다.

예외	HTTP Status	상황
GameNotFoundException	404	존재하지 않는 게임
GameFinishedException	409	이미 종료된 게임
Validation 오류	400	잘못된 요청 데이터

**종료된 게임의 진행 저장**

PUT /games/{gameId}/progress
            ↓
       Game 조회
            ↓
     isFinished() 확인
            ↓
       ┌────┴────┐
       ↓         ↓
      true      false
       ↓         ↓
GameFinished   진행 데이터
Exception       저장
       ↓
     409

GameFinishedException은 실제 데이터 변경 전에 발생하기 때문에 종료된 게임의 진행 정보가 변경되지 않습니다.

📡 **API**

Method	Endpoint	설명
POST	/games	게임 생성
GET	/games	게임 목록 조회
GET	/games/{gameId}	게임 상세 조회
PUT	/games/{gameId}/progress	게임 진행 저장
DELETE	/games/{gameId}	게임 삭제

🚀 **실행 방법**

1. 프로젝트 Clone
git clone <repository-url>
cd <project-directory>
2. MySQL 실행
Docker 또는 로컬 MySQL 환경에서 데이터베이스를 실행합니다.

3. 서버 실행
Windows:
gradlew.bat bootRun
Mac / Linux:
./gradlew bootRun

5. API 테스트
Postman을 이용하여 다음 주소로 API를 호출합니다.

http://localhost:8080

📚 **학습 및 적용 내용**

이 프로젝트를 통해 다음 내용을 학습하고 적용했습니다.

Spring Boot REST API
Controller / Service / Repository 3-Layer Architecture
Spring Data JPA
Entity와 DTO 분리
Bean Validation
JPA Dirty Checking
@Transactional
연관 데이터 관리
예외 처리 및 GlobalExceptionHandler
HTTP 상태 코드에 따른 API 응답 처리
