package jetsennet.jbmp.trap.receiver;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.concurrent.ExecutorService;

import org.snmp4j.CommandResponder;
import org.snmp4j.MessageDispatcher;
import org.snmp4j.MessageException;
import org.snmp4j.PDU;
import org.snmp4j.TransportMapping;
import org.snmp4j.asn1.BERInputStream;
import org.snmp4j.mp.MessageProcessingModel;
import org.snmp4j.mp.PduHandle;
import org.snmp4j.mp.PduHandleCallback;
import org.snmp4j.mp.StateReference;
import org.snmp4j.mp.StatusInformation;
import org.snmp4j.smi.Address;
import org.snmp4j.util.WorkerTask;

/**
 * 使用多线程处理传入信息的MessageDispatcher。 
 * 替换了org.snmp4j.util.MultiThreadedMessageDispatcher中的线程池，使用java自带的线程池。
 * @author Guo
 */
public class ThreadPoolMessageDispatcher implements MessageDispatcher
{

    private MessageDispatcher dispatcher;
    private ExecutorService threadPool;

    /**
     * @param threadPool 参数
     * @param decoratedDispatcher 参数
     */
    public ThreadPoolMessageDispatcher(ExecutorService threadPool, MessageDispatcher decoratedDispatcher)
    {
        this.threadPool = threadPool;
        this.dispatcher = decoratedDispatcher;
    }

    public int getNextRequestID()
    {
        return dispatcher.getNextRequestID();
    }

    /**
     * @param model 参数
     */
    public void addMessageProcessingModel(MessageProcessingModel model)
    {
        dispatcher.addMessageProcessingModel(model);
    }

    /**
     * @param model 参数
     */
    public void removeMessageProcessingModel(MessageProcessingModel model)
    {
        dispatcher.removeMessageProcessingModel(model);
    }

    /**
     * @param messageProcessingModel 参数
     * @return 结果
     */
    public MessageProcessingModel getMessageProcessingModel(int messageProcessingModel)
    {
        return dispatcher.getMessageProcessingModel(messageProcessingModel);
    }

    /**
     * @param transport 参数
     */
    public void addTransportMapping(TransportMapping transport)
    {
        dispatcher.addTransportMapping(transport);
    }

    /**
     * @param transport 参数
     * @return 结果
     */
    public TransportMapping removeTransportMapping(TransportMapping transport)
    {
        return dispatcher.removeTransportMapping(transport);
    }

    public Collection getTransportMappings()
    {
        return dispatcher.getTransportMappings();
    }

    @Override
    public void addCommandResponder(CommandResponder listener)
    {
        dispatcher.addCommandResponder(listener);
    }

    @Override
    public void removeCommandResponder(CommandResponder listener)
    {
        dispatcher.removeCommandResponder(listener);
    }

    @Override
    public PduHandle sendPdu(Address transportAddress, int messageProcessingModel, int securityModel, byte[] securityName, int securityLevel,
            PDU pdu, boolean expectResponse) throws MessageException
    {
        return dispatcher.sendPdu(transportAddress, messageProcessingModel, securityModel, securityName, securityLevel, pdu, expectResponse);
    }

    @Override
    public PduHandle sendPdu(TransportMapping transportMapping, Address transportAddress, int messageProcessingModel, int securityModel,
            byte[] securityName, int securityLevel, PDU pdu, boolean expectResponse) throws MessageException
    {
        return dispatcher.sendPdu(transportMapping, transportAddress, messageProcessingModel, securityModel, securityName, securityLevel, pdu,
            expectResponse);
    }

    @Override
    public PduHandle sendPdu(TransportMapping transportMapping, Address transportAddress, int messageProcessingModel, int securityModel,
            byte[] securityName, int securityLevel, PDU pdu, boolean expectResponse, PduHandleCallback callback) throws MessageException
    {
        return dispatcher.sendPdu(transportMapping, transportAddress, messageProcessingModel, securityModel, securityName, securityLevel, pdu,
            expectResponse, callback);
    }

    @Override
    public int returnResponsePdu(int messageProcessingModel, int securityModel, byte[] securityName, int securityLevel, PDU pdu,
            int maxSizeResponseScopedPDU, StateReference stateReference, StatusInformation statusInformation) throws MessageException
    {
        return dispatcher.returnResponsePdu(messageProcessingModel, securityModel, securityName, securityLevel, pdu, maxSizeResponseScopedPDU,
            stateReference, statusInformation);
    }

    @Override
    public void processMessage(TransportMapping sourceTransport, Address incomingAddress, BERInputStream wholeMessage)
    {
        // OK, here wo do all that what this class is all about!
        MessageTask task = new MessageTask(sourceTransport, incomingAddress, wholeMessage);
        threadPool.execute(task);
    }

    @Override
    public void processMessage(TransportMapping sourceTransport, Address incomingAddress, ByteBuffer wholeMessage)
    {
        processMessage(sourceTransport, incomingAddress, new BERInputStream(wholeMessage.duplicate()));
    }

    @Override
    public void releaseStateReference(int messageProcessingModel, PduHandle pduHandle)
    {
        dispatcher.releaseStateReference(messageProcessingModel, pduHandle);
    }

    @Override
    public TransportMapping getTransport(Address destAddress)
    {
        return dispatcher.getTransport(destAddress);
    }

    class MessageTask implements WorkerTask
    {

        private TransportMapping sourceTransport;
        private Address incomingAddress;
        private BERInputStream wholeMessage;

        public MessageTask(TransportMapping sourceTransport, Address incomingAddress, BERInputStream wholeMessage)
        {
            this.sourceTransport = sourceTransport;
            this.incomingAddress = incomingAddress;
            this.wholeMessage = wholeMessage;
        }

        public void run()
        {
            dispatcher.processMessage(sourceTransport, incomingAddress, wholeMessage);
        }

        public void terminate()
        {
        }

        public void join() throws InterruptedException
        {
        }

        public void interrupt()
        {
        }
    }
}
