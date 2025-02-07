package org.firstinspires.ftc.teamcode.Autonomous;

import com.arcrobotics.ftclib.command.CommandScheduler;
import com.arcrobotics.ftclib.command.SequentialCommandGroup;
import com.arcrobotics.ftclib.geometry.Pose2d;
import com.arcrobotics.ftclib.geometry.Rotation2d;
import com.arcrobotics.ftclib.trajectory.Trajectory;
import com.arcrobotics.ftclib.trajectory.TrajectoryConfig;
import com.arcrobotics.ftclib.trajectory.TrajectoryGenerator;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.Autonomous.AutonomousIndex.RamsetteCommand;
import org.firstinspires.ftc.teamcode.Commands.Baskets.AutoHighBasket;
import org.firstinspires.ftc.teamcode.Commands.Intake.MoveIntake;
import org.firstinspires.ftc.teamcode.Commands.StowAll;
import org.firstinspires.ftc.teamcode.Subsystems.Chassis;
import org.firstinspires.ftc.teamcode.Subsystems.Constants;
import org.firstinspires.ftc.teamcode.Subsystems.Elevator;
import org.firstinspires.ftc.teamcode.Subsystems.Arm;
import org.firstinspires.ftc.teamcode.Subsystems.Intake;
import org.firstinspires.ftc.teamcode.Subsystems.Wrist;

import java.util.Arrays;


@Autonomous
public class HighBasketAndPark extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {

        Chassis chassis = new Chassis(hardwareMap);
        Elevator elevator= new Elevator(hardwareMap);
        Arm arm = new Arm(hardwareMap);
        Wrist wrist = new Wrist(hardwareMap);
        Intake intake = new Intake(hardwareMap);


        //Forward
        TrajectoryConfig ForwardConfig = new TrajectoryConfig(0.5,0.2);
        ForwardConfig.setReversed(false);

        //Backward
        TrajectoryConfig ReverseConfig = new TrajectoryConfig(0.5,0.2);
        ReverseConfig.setReversed(true);

        Trajectory First = TrajectoryGenerator.generateTrajectory(Arrays.asList(
                new Pose2d(0.0,0.0,Rotation2d.fromDegrees(0)),
                new Pose2d(1.15, -0.23,Rotation2d.fromDegrees(45))), ForwardConfig
        );

        Trajectory Second = TrajectoryGenerator.generateTrajectory(Arrays.asList(
                new Pose2d(1.07,-0.15,Rotation2d.fromDegrees(45)),
                new Pose2d(1.15, -0.23,Rotation2d.fromDegrees(45))), ForwardConfig
        );

        Trajectory Third = TrajectoryGenerator.generateTrajectory(Arrays.asList(
                new Pose2d(1.15,-0.23,Rotation2d.fromDegrees(-135)),
                new Pose2d(1.07, -0.15,Rotation2d.fromDegrees(0))), ReverseConfig
        );



        SequentialCommandGroup FirstCommandGroup = new SequentialCommandGroup(

                new RamsetteCommand(chassis, First),
                new AutoHighBasket(arm, elevator, wrist),
                // new RamsetteCommand(chassis, Second),
                new MoveIntake(intake, Constants.Intake.INTAKE_OPEN).withTimeout(1500),
                new StowAll(arm, elevator, wrist)
                /*new RamsetteCommand(chassis, Third),
                new StowAll(arm, elevator, wrist),
                new RamsetteCommand(chassis, Third),
                new GroundGrabMedium(arm, elevator, wrist),
                new MoveIntake(intake, Constants.Intake.INTAKE_STOW),
                new StowAll(arm, elevator, wrist),
                new TurnToAngle(chassis, Rotation2d.fromDegrees(48)),
                new WaitCommand(1000),
                new HighBasket(arm, elevator, wrist),
                new MoveIntake(intake, Constants.Intake.INTAKE_OPEN),//Pending Test
                new StowAll(arm, elevator, wrist),
                new MoveIntake(intake, Constants.Intake.INTAKE_STOW)*/

        );


        waitForStart();
        chassis.reset(new Pose2d());
        CommandScheduler.getInstance().schedule(FirstCommandGroup);

        while (opModeIsActive ()){
            CommandScheduler.getInstance().run();

            Pose2d pose = chassis.getPose();
            telemetry.addData("X", pose.getX());
            telemetry.addData("Y", pose.getY());
            telemetry.addData("Heading", pose.getRotation().getDegrees());

            telemetry.addLine("--- Chassis Telemetry ---");
            telemetry.addData("RightDistance", chassis.rightDistance());
            telemetry.addData("LeftDistance", chassis.leftDistance());
            telemetry.update();
        }
    }
}