package com.schneewittchen.rosandroid.widgets.joystick;

import android.view.View;

import com.schneewittchen.rosandroid.widgets.base.BaseData;
import com.schneewittchen.rosandroid.widgets.base.BaseEntity;
import com.schneewittchen.rosandroid.widgets.base.BaseNode;

import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import geometry_msgs.Vector3;


/**
 * TODO: Description
 *
 * @author Nico Studt
 * @version 1.0.1
 * @created on 13.03.20
 * @updated on 15.04.20
 * @modified by
 */
public class JoystickNode extends BaseNode<WidgetJoystickEntity> {

    private float lastX, lastY;
    Publisher<geometry_msgs.Twist> publisher;


    public JoystickNode(BaseEntity widget) {
        super((WidgetJoystickEntity) widget);
    }


    @Override
    public void onStart(ConnectedNode connectedNode) {
        publisher = connectedNode.newPublisher(widget.publisher.topic, widget.publisher.messageType);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                publish();
            }
        }, 100, 500);
    }

    private void publish() {
        float xAxisValue = widget.xScaleLeft  + (widget.xScaleRight - widget.xScaleLeft) * ((lastX+1) /2f);
        float yAxisValue = widget.yScaleLeft  + (widget.yScaleRight - widget.yScaleLeft) * ((lastY+1) /2f);

        geometry_msgs.Twist currentTwistMessage = publisher.newMessage();

        for (int i = 0; i < 2; i++) {
            String[] splitMapping = (i == 0? widget.xAxisMapping : widget.yAxisMapping).split("/");
            float value = i == 0? xAxisValue : yAxisValue;

            Vector3 dirVector;
            if (splitMapping[0].equals("Linear")) {
                dirVector = currentTwistMessage.getLinear();
            } else {
                dirVector = currentTwistMessage.getAngular();
            }

            switch (splitMapping[1]) {
                case "X":
                    dirVector.setX(value);
                    break;
                case "Y":
                    dirVector.setY(value);
                    break;
                case "Z":
                    dirVector.setZ(value);
                    break;
            }
        }

        publisher.publish(currentTwistMessage);
    }

    @Override
    public void onNewData(BaseData data) {
        JoystickData joystickData = (JoystickData) data;
        lastX = joystickData.x;
        lastY = joystickData.y;

        System.err.println(lastX + " "  + lastY);
    }
}
