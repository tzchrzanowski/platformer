package components;

import domo.Component;

public class SpriteRenderer extends Component {
    private boolean firstTime = false;

    @Override
    public void start() {
        if(firstTime == false) {
            System.out.println("i am starting");
            firstTime = true;
        }
    }

    @Override
    public void update(float dt) {
        System.out.println("i am updating");
    }
}
