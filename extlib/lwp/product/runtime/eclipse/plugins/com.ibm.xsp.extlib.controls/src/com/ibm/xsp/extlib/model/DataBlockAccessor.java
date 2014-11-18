/*
 * © Copyright IBM Corp. 2010
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing 
 * permissions and limitations under the License.
 */

package com.ibm.xsp.extlib.model;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.FacesExceptionEx;
import com.ibm.xsp.context.FacesContextEx;
import com.ibm.xsp.extlib.model.DataAccessorSource.Container;



/**
 * Object that encapsulates data access.
 * <p>
 * This object is specifically designed to work with an extended iterator data model.<br/>
 * A DataAccessor has 3 modes of working:
 * <ul>
 *  <li>Live access to data (just using count and get)
 *  <li>Live access to data block - one block is retrieved at a time
 *  <li>Cached access to data, until the cache fills up...
 * </ul>
 * </p>
 * @author Philippe Riand
 */
public abstract class DataBlockAccessor extends DataAccessor implements Externalizable {

    private static final long serialVersionUID = 1L;
    
    public static abstract class Block implements Externalizable {
        private static final long serialVersionUID = 1L;
        int         generationId;
        Block       prev;
        Block       next;
        int         index;
        long        creationTime;
        public Block() {} // Serialization
        public Block(int index) {
            this.index = index;
            this.creationTime = System.currentTimeMillis();
        }
        public abstract int getLength();
        public abstract Object getData(int index);

        public boolean isExpired(int timeout) {
            if(timeout>0) {
                long now = System.currentTimeMillis();
                return now>(creationTime+(((long)timeout)*1000L));
            }
            return false;
        }
        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeInt(index);
            out.writeLong(creationTime);
        }
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            index = in.readInt();
            creationTime = in.readLong();
        }
    }
    public static class ArrayBlock extends Block {
        private static final long serialVersionUID = 1L;
        Object[]    data;
        public ArrayBlock() {} // Serialization
        public ArrayBlock(int index, Object[] data) {
            super(index);
            this.data = data;
        }
        @Override
        public int getLength() {
            return data!=null ? data.length : 0;
        }
        @Override
        public Object getData(int index) {
            return data[index];
        }
        
        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
            super.writeExternal(out);
            out.writeObject(data);
        }
        @Override
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            super.readExternal(in);
            data = (Object[])in.readObject();
        }
    }
    public static class EmptyBlock extends Block {
        private static final long serialVersionUID = 1L;
        public EmptyBlock() {} // Serialization
        public EmptyBlock(int index) {
            super(index);
        }
        @Override
        public boolean isExpired(int timeout) {
            return false;
        }
        @Override
        public int getLength() {
            return 0;
        }
        @Override
        public Object getData(int index) {
            return null;
        }
    }
    private Block firstBlock;
    private int blockCount;
    private int blockSize;
    private int maxBlockCount;
    
    private int maxCount;
    private int totalCount;

    private int timeout;

    private transient boolean shouldRecomputeTotal;
    
    public DataBlockAccessor() {
    }
    public DataBlockAccessor(DataAccessorBlockSource dataSource, int maxBlockCount) {
        super(dataSource);
        this.blockSize = 20; // Arbitrary first value
        this.maxCount = -1;
        this.totalCount = -1;
        this.timeout = dataSource.getTimeout();
        
        this.maxBlockCount = maxBlockCount;
        if(maxBlockCount<=0) {
            this.maxBlockCount = getDefaultMaxBlockCount();
        }
        
        
    }
    
    protected int getMaxBlockCount() {
        return maxBlockCount;
    }
    protected int getDefaultMaxBlockCount() {
        return 0;
    }
    

    public int getMaxCount() {
        return maxCount;
    }
    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }
    
    public int getTotalCount() {
        return totalCount;
    }
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
    
    public int getTimeout() {
        return timeout;
    }
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    
    //////////////////////////////////////////////////////////////////////
    // Required methods
    //////////////////////////////////////////////////////////////////////

    //
    // Data access
    //
    @Override
    public int getCount() {
        if(totalCount>=0) {
            return totalCount;
        }
        if(!shouldRecomputeTotal) {
            if(maxCount>=0) {
                return maxCount;
            }
        }
        return -1;
    }

    @Override
    public Object get(int index) {
        Block b = findBlockForRow(index, true);
        if(b!=null) {
            int idx = index-(index/blockSize)*blockSize;
            if(idx<b.getLength()) {
                return b.getData(idx);
            }
        }
        return null; // Not loaded...
    }
    

    //////////////////////////////////////////////////////////////////////
    // Serialization
    //////////////////////////////////////////////////////////////////////
    
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(blockCount);
        out.writeInt(blockSize);
        out.writeInt(maxBlockCount);
        out.writeInt(maxCount);
        out.writeInt(totalCount);
        out.writeInt(timeout);
        for(Block b=firstBlock; b!=null; b=b.next) {
            out.writeObject(b);
        }
    }
    
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        blockCount = in.readInt();
        blockSize = in.readInt();
        maxBlockCount = in.readInt();
        maxCount = in.readInt();
        totalCount = in.readInt();
        timeout = in.readInt();
        
        Block lastBlock = firstBlock = null;
        for(int i=0; i<blockCount; i++) {
            Block b = (Block)in.readObject();
            if(firstBlock==null) {
                firstBlock = b; 
            } else {
                lastBlock.next = b;
                b.prev = lastBlock;
            }
            lastBlock = b;
            blockLoaded(b);
        }
    }
    
    protected void blockLoaded(Block b) {
    }
    
    
    //////////////////////////////////////////////////////////////////////
    // Optional methods
    //////////////////////////////////////////////////////////////////////
    
    @Override
    public boolean handlePrefetch() {
        return true;
    }
    
    @Override
    public boolean isPrefetched(int index) {
        Block b = findBlockForRow(index,false);
        return b!=null;
    }

    @Override
    public void prefetchData(int start, int count) {
        // We should discard the current cache if the caller asks for a different block count
        // start should generally be a multiple of count...
        if(count!=blockSize && count>0) {
            clearData(true);
            blockSize = count;
        }
        if(blockSize == 0){
            ArithmeticException ae = new ArithmeticException("Divide by zero error");// $NLX-DataBlockAccessor_DivideByZeroError-1$
            String zeroArgConstr = "com.ibm.xsp.extlib.model.DataBlockAccessor.DataBlockAccessor()"; //$NON-NLS-1$
            String twoArgConstr = "com.ibm.xsp.extlib.model.DataBlockAccessor.DataBlockAccessor(DataAccessorSource, int)"; //$NON-NLS-1$
            String exMsg = "Blocksize cannot be zero. The {0} zero argument constructor should only be used during serialization. Please use the constructor {1}."; // $NLX-DataBlockAccessor_BlocksizeZeroSoWrongConstructor-1$
            throw new FacesExceptionEx(ae, StringUtil.format(exMsg, zeroArgConstr, twoArgConstr));
        }
        // Now, find the block and load it if necessary
        // Note that pre-fetch is just an indicator here, so 'count' might not be fulfilled 
        findBlockByIndex(start/blockSize, true);
    }
    
    protected Block findBlockForRow(int rowIndex, boolean load) {
        return findBlockByIndex(rowIndex/blockSize,load);
    }
    protected Block findBlockByIndex(int blockIndex, boolean load) {
        FacesContextEx ctx = FacesContextEx.getCurrentInstance();
        this.shouldRecomputeTotal = false;
        
        // Check the first block, as we don't have to move it around
        if(firstBlock!=null) {
            // Look for the other ones
            for(Block b=firstBlock; b!=null; b=b.next) {
                if(b.index==blockIndex) {
                    // If it is expired, we discard it but only:
                    //  - During the rendering phase
                    //  - If it hasn't been already read in this phase (based on the rendering sequence
                    if(b.isExpired(getTimeout())) {
                        if(ctx.isRenderingPhase()) {
                            Container dc = getDataSource().getDataContainer(ctx);
                            if(dc!=null && b.generationId!=dc.getGenerationId()) {
                                if(b.prev!=null) {
                                    b.prev.next = b.next;
                                } else {
                                    firstBlock = b.next;
                                }
                                if(b.next!=null) {
                                    b.next.prev = b.prev;
                                }
                                blockCount--;
                                break;
                            }
                        }
                    }
                    // Else, make it the first the block if not already
                    if(firstBlock!=b) {
                        // Move it at the first place
                        b.prev.next = b.next;
                        if(b.next!=null) {
                            b.next.prev = b.prev;
                        }
                        b.prev = null;
                        b.next = firstBlock;
                        firstBlock = b.next.prev = b;
                    }
                    return b;
                }
            }
        }
        // Ok, the block is not there, so we should load it
        if(load) {
            Block b = loadBlock(blockIndex,blockSize);
            if(b!=null) {
                // Store the rendering sequence if there is a timeout
                if(getTimeout()>0) {
                    Container dc = getDataSource().getDataContainer(ctx);
                    if(dc!=null) {
                        b.generationId = dc.getGenerationId();
                    }
                    b.creationTime = System.currentTimeMillis();
                }
                // Put it at the first place
                b.prev = null; 
                b.next = firstBlock;
                if(firstBlock!=null) {
                    firstBlock.prev = b;
                }
                firstBlock = b;
                blockCount++;
                
                // And remove the last one, if there are too many
                if(blockCount>getMaxBlockCount()) {
                    Block last = firstBlock.next ;
                    if(last!=null) {
                        while(last.next!=null) {last=last.next;}
                        last.prev.next = null;
                        blockCount--;
                    }
                }
                blockLoaded(b);
                
                // Update the max count
                // Try to estimate the row count
                //  totalCount: when we are sure about the count
                //  maxCount: the maximum we saw so far + 1(so the navigator displays an extra page)
                if(totalCount<0) {
                    int c = b.getLength();
                    if(c!=blockSize) {
                        // The end had been reached, this is the maximum count...
                        totalCount = blockIndex*blockSize+c;
                    } else {
                        // We add one, as we we might have another entry available
                        maxCount = Math.max(maxCount, blockIndex*blockSize+c+1);
                    }
                }
                return b;
            }
        }
        
        return null;
    }
    protected abstract Block loadBlock(int index, int blockSize);
    
    @Override
    public void clearData(boolean recomputeCount) {
        blockCount = 0;
        firstBlock = null;
        if(recomputeCount) {
            shouldRecomputeTotal = true;
        }
    }
    
    @Override
    public void updateCount() {
        shouldRecomputeTotal = true;
    }
    
    //
    // More rows management
    // This is an extended way for counting entries, when the exact number of rows
    // is not known, and the pager needs to display ... if more rows are available 
    //
    @Override
    public boolean canHaveMoreRows() {
        return true;
    }

    @Override
    public int hasMoreRows(int maxCount) {
        if(totalCount>=0) {
            return totalCount;
        }
        return this.maxCount; // Ok, this only what we know so far
    }
}
