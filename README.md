# hunstagram
인스타그램 API 클론코딩 📷 - API 설계부터 배포 자동화까지

## Goals
- (Jacoco) 테스트 커버리지 80% 이상 
- (Swagger) API 명세 작성
- (AWS S3) 이미지 저장 및 조회
- (Jenkins, Docker, AWC EC2) Docker 사용 컨테이너 배포 및 CI/CD
- 도메인 연결
- DTO inner class 처리 등 클린코드에 집중하며 코드 작성
- etc..

## ERD
![huns](https://user-images.githubusercontent.com/71416677/201517195-9015ea2a-cf76-47dc-828d-64c774c7fafa.png)


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
