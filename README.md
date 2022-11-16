# hunstagram
인스타그램 API 클론코딩 📷 - API 설계부터 배포 자동화까지

## Goals
- (Jacoco) Domain 관련 Controller, Service 단에 대해 테스트 커버리지 100%
- 통합테스트, 유닛테스트 작성
- (Swagger) API 명세 작성
- (AWS S3) 이미지 저장 및 조회
- (Jenkins, Docker, AWC EC2) Docker 사용 컨테이너 배포 및 CI/CD
- 도메인 연결
- DTO inner class 처리 등 클린코드에 집중하며 코드 작성
- etc..

## ERD
<img width="1039" alt="image" src="https://user-images.githubusercontent.com/71416677/201517254-9b72c7bc-1cf1-454a-99e9-b0bf190a96cd.png">


## 커밋 컨벤션
- feat: 새로운 기능의 추가
- remove: 파일, 코드 삭제 또는 기능 삭제
- fix: 버그 수정
- docs: 문서 수정
- style: 스타일 관련 수정 (코드 포맷팅, 세미콜론 누락, 코드 자체의 변경이 없는 경우)
- refactor: 코드 리팩토링
- test: 테스트 관련 코드
- chore: 빌드 관련 수정 (application.yml, build.gradle, .gitignore ..)

## 새롭게 알게된 것 
- Entity Class에 @NoArgsConstructor(access=PROTECTED) 
- Mock S3 라이브러리 사용하여 로컬 환경에서 S3 테스트
- Multipart 테스트 (Controller Unit Test, Service Integration Test)
