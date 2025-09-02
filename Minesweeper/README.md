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
- Game 자체(Game), Board 구성(Board), 현 Board 상태 출력(Renderer), Square -> cell, 입력 처리(InputHandler), 입력에 의한 동작(Command)
- Game 난이도 설정 가능하게 확장.(Level)
- 주위의 폭탄 수에 따른 value 설정 방식 변경
    (주위 8칸을 탐색해서 바로 val 설정하는 방식) -> 
    (별도 int[][]를 두고, 폭탄 기준으로 주위 8칸에 +1 누적)
- Cell 내의 value 변수 타입을 String-> int 로 변경.
- 변수 타입 변경에 따라 별도의 int[][]를 두지 않고 직접 누적.