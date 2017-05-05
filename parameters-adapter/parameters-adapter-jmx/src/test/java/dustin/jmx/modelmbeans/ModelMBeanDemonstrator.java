package dustin.jmx.modelmbeans;

import java.lang.management.ManagementFactory;
import javax.management.Descriptor;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanParameterInfo;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.RuntimeOperationsException;
import javax.management.modelmbean.DescriptorSupport;
import javax.management.modelmbean.InvalidTargetObjectTypeException;
import javax.management.modelmbean.ModelMBean;
import javax.management.modelmbean.ModelMBeanInfoSupport;
import javax.management.modelmbean.ModelMBeanOperationInfo;
import javax.management.modelmbean.RequiredModelMBean;

/**
 * The purpose of this class is to demonstrate use of ModelMBeans in the raw.
 */
public class ModelMBeanDemonstrator
{
    private MBeanServer mbeanServer;

    /**
     * Constructor.
     */
    public ModelMBeanDemonstrator()
    {
        this.mbeanServer = ManagementFactory.getPlatformMBeanServer();
    }

    /**
     * Construct the meta information for the SimpleCalculator ModelMBean
     * operations and the operations' parameters.
     *
     * @return
     */
    private ModelMBeanOperationInfo[] buildModelMBeanOperationInfo()
    {
        //
        // Build the PARAMETERS and OPERATIONS meta information for "add".
        //

        final MBeanParameterInfo augendParameter =
                new MBeanParameterInfo(
                        "augend",
                        Integer.TYPE.toString(),
                        "The first parameter in the addition (augend)." );
        final MBeanParameterInfo addendParameter =
                new MBeanParameterInfo(
                        "addend",
                        Integer.TYPE.toString(),
                        "The second parameter in the addition (addend)." );

        final ModelMBeanOperationInfo addOperationInfo =
                new ModelMBeanOperationInfo(
                        "add",
                        "Integer Addition",
                        new MBeanParameterInfo[] {augendParameter, addendParameter},
                        Integer.TYPE.toString(),
                        ModelMBeanOperationInfo.INFO );


        //
        // Build the PARAMETERS and OPERATIONS meta information for "subtract".
        //

        final MBeanParameterInfo minuendParameter =
                new MBeanParameterInfo(
                        "minuend",
                        Integer.TYPE.toString(),
                        "The first parameter in the substraction (minuend)." );

        final MBeanParameterInfo subtrahendParameter =
                new MBeanParameterInfo(
                        "subtrahend",
                        Integer.TYPE.toString(),
                        "The second parameter in the subtraction (subtrahend)." );

        final ModelMBeanOperationInfo subtractOperationInfo =
                new ModelMBeanOperationInfo(
                        "subtract",
                        "Integer Subtraction",
                        new MBeanParameterInfo[] {minuendParameter, subtrahendParameter},
                        Integer.TYPE.toString(),
                        ModelMBeanOperationInfo.INFO );

        //
        // Build the PARAMETERS and OPERATIONS meta information for "multiply".
        //

        final MBeanParameterInfo factorOneParameter =
                new MBeanParameterInfo(
                        "factor1",
                        Integer.TYPE.toString(),
                        "The first factor in the multiplication." );

        final MBeanParameterInfo factorTwoParameter =
                new MBeanParameterInfo(
                        "factor2",
                        Integer.TYPE.toString(),
                        "The second factor in the multiplication." );

        final ModelMBeanOperationInfo multiplyOperationInfo =
                new ModelMBeanOperationInfo(
                        "multiply",
                        "Integer Multiplication",
                        new MBeanParameterInfo[] {factorOneParameter, factorTwoParameter},
                        Integer.TYPE.toString(),
                        ModelMBeanOperationInfo.INFO );

        //
        // Build the PARAMETERS and OPERATIONS meta information for "divide".
        //

        final MBeanParameterInfo dividendParameter =
                new MBeanParameterInfo(
                        "dividend",
                        Integer.TYPE.toString(),
                        "The dividend in the division." );

        final MBeanParameterInfo divisorParameter =
                new MBeanParameterInfo(
                        "divisor",
                        Integer.TYPE.toString(),
                        "The divisor in the division." );

        final ModelMBeanOperationInfo divideOperationInfo =
                new ModelMBeanOperationInfo(
                        "divide",
                        "Integer Division",
                        new MBeanParameterInfo[] {dividendParameter, divisorParameter},
                        Double.TYPE.toString(),
                        ModelMBeanOperationInfo.INFO );

        return new ModelMBeanOperationInfo[]
                { addOperationInfo, subtractOperationInfo,
                        multiplyOperationInfo, divideOperationInfo };
    }

    /**
     * Build descriptor.
     *
     * @return Generated descriptor.
     */
    private Descriptor buildDescriptor()
    {
        final Descriptor descriptor = new DescriptorSupport();
        descriptor.setField("name", "ModelMBeanInTheRaw");
        descriptor.setField("descriptorType", "mbean");
        return descriptor;
    }

    /**
     * Set the provided ModelMBean to manage the SimpleCalculator resource.
     *
     * @param modelMBeanToManageResource ModelMBean to manage the SimpleCalculator
     *    resource.
     */
    private void setModelMBeanManagedResource(
            final ModelMBean modelMBeanToManageResource )
    {
        try
        {
            modelMBeanToManageResource.setManagedResource(
                    new SimpleCalculator(),
                    "ObjectReference");
        }
        catch (RuntimeOperationsException ex)
        {
            System.err.println(ex.getMessage());
        }
        catch (InstanceNotFoundException ex)
        {
            System.err.println(ex.getMessage());
        }
        catch (InvalidTargetObjectTypeException ex)
        {
            System.err.println(ex.getMessage());
        }
        catch (MBeanException ex)
        {
            System.err.println(ex.getMessage());
        }
    }

    /**
     * Create a ModelMBean the "old fashioned" way.
     *
     * @return ModelMBean created directly without framework.
     */
    private ModelMBean createRawModelMBean()
    {
        RequiredModelMBean modelmbean = null;
        try
        {
            final ModelMBeanInfoSupport modelMBeanInfo =
                    new ModelMBeanInfoSupport(
                            SimpleCalculator.class.getName(),
                            "A simple integer calculator.",
                            null,      // attributes
                            null,      // constructors
                            buildModelMBeanOperationInfo(),
                            null,      // notifications
                            buildDescriptor() );
            modelmbean = new RequiredModelMBean(modelMBeanInfo);
            setModelMBeanManagedResource(modelmbean);
        }
        catch (MBeanException mbeanEx)
        {
            System.err.println(  "ERROR trying to create a ModelMBean:\n"
                    + mbeanEx.getMessage() );
        }

        return modelmbean;
    }

    /**
     * Register the provided ModelMBean in the MBeanServer using the provided
     * ObjectName String.
     *
     * @param modelMBean ModelMBean to be registered with the MBeanServer.
     * @param objectNameString ObjectName of the registered ModelMBean.
     */
    private void registerModelMBean(
            final ModelMBean modelMBean,
            final String objectNameString)
    {
        try
        {
            final ObjectName objectName =
                    new ObjectName(objectNameString);
            this.mbeanServer.registerMBean( modelMBean,
                    objectName );
        }
        catch (MalformedObjectNameException badObjectName)
        {
            System.err.println(  "ERROR trying to generate ObjectName based on "
                    + objectNameString + ":\n"
                    + badObjectName.getMessage() );
        }
        catch (InstanceAlreadyExistsException objectNameAlreadyExists)
        {
            System.err.println(  "ERROR due to ObjectName ["
                    + objectNameString + "] already existing: "
                    + objectNameAlreadyExists.getMessage() );
        }
        catch (MBeanRegistrationException badMBeanRegistration)
        {
            System.err.println(  "ERROR trying to register MBean with ObjectName "
                    + objectNameString + ":\n"
                    + badMBeanRegistration.getMessage() );
        }
        catch (NotCompliantMBeanException nonCompliantMBean)
        {
            System.err.println(  "ERROR trying to register nonconforming MBean "
                    + "with ObjectName of " + objectNameString
                    + nonCompliantMBean.getMessage() );
        }
    }

    /**
     * Create a ModelMBean in the raw and register it with the MBeanServer.
     */
    public void createAndRegisterRawModelMBean()
    {
        final String rawModelMBeanObjectNameString = "modelmbean:type=raw";
        registerModelMBean(createRawModelMBean(), rawModelMBeanObjectNameString);
    }

    /**
     * Pause for the specified number of milliseconds.
     *
     * @param millisecondsToPause Milliseconds to pause execution.
     */
    public static void pause(final int millisecondsToPause)
    {
        try
        {
            Thread.sleep(millisecondsToPause);
        }
        catch (InterruptedException threadAwakened)
        {
            System.err.println("Don't wake me up!\n" + threadAwakened.getMessage());
        }
    }

    /**
     * Demonstate use of ModelMBean.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        final ModelMBeanDemonstrator me = new ModelMBeanDemonstrator();
        me.createAndRegisterRawModelMBean();
        pause(1000000);
    }
}