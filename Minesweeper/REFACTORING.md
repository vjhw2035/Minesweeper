# 리팩토링
- App.java에서 구현 완료
- Board 클래스에 너무 집중되어 있어 Refactoring.java에서 리팩토링.
- Game 자체(Game), Board 구성(Board), 현 Board 상태 출력(Renderer), Square -> cell, 입력 처리(InputHandler), 입력에 의한 동작(Command)
- Game 난이도 설정 가능하게 확장.(Level)
- 주위의 폭탄 수에 따른 value 설정 방식 변경
    (주위 8칸을 탐색해서 바로 val 설정하는 방식) -> 
    (별도 int[][]를 두고, 폭탄 기준으로 주위 8칸에 +1 누적)
- Cell 내의 value 변수 타입을 String-> int 로 변경.
- 폭탄 = -1, 주위 폭탄 수에 따라 0~8.
- 변수 타입 변경에 따라 별도의 int[][]를 두지 않고 직접 누적.
- Cell의 상태를 CellState로 선언하여 사용 -> 그에 따라 opened와 flagged를 삭제
- InputHandler의 getCommand에서 예외 처리 필요
    1. IOException
    2. null 입력
    3. 잘못된 형식/좌표/command
- 예외 처리 완료 및 입력 처리 완료


## 학습 사항
- 각 클래스마다 역할 분담을 확실하게 해야 함. 유지, 보수, 가독성 등등에서 차이가 많이 남.
- 오류와 예외의 차이와 예외의 종류들
- checked와 unchecked Exception의 차이와 예외들의 처리 방법
- 패키지 구조로 리팩토링 버전 관리하기