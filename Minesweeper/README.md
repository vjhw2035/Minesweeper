# Minesweeper (지뢰찾기)

자바 콘솔 기반 지뢰찾기 프로젝트입니다.

## 기능
- 랜덤 지뢰 배치
- 칸 열기 / 깃발 표시
- 승리 & 패배 판정

## 실행 방법
```bash
javac src/Main.java
java src/Main

## 리팩토링
- App.java에서 구현 완료
- Board 클래스에 너무 집중되어 있어 Refactoring.java에서 리팩토링.
- Game 자체, Board 구성, 현 Board 상태 출력, Square, 입력 처리, 입력에 의한 동작
- Game 난이도 설정 가능하게 확장.