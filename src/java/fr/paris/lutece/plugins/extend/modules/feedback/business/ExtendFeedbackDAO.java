/*
 * Copyright (c) 2002-2022, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */

package fr.paris.lutece.plugins.extend.modules.feedback.business;

import fr.paris.lutece.plugins.extend.business.extender.ResourceExtenderDTO;
import fr.paris.lutece.plugins.extend.business.extender.history.ResourceExtenderHistory;
import fr.paris.lutece.plugins.extend.modules.feedback.util.constants.FeedbackConstants;
import fr.paris.lutece.plugins.extend.service.extender.history.IResourceExtenderHistoryService;
import fr.paris.lutece.plugins.extend.service.extender.history.ResourceExtenderHistoryService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import java.sql.Statement;
import java.sql.Timestamp;

/**
 * This class provides Data Access methods for ExtendFeedback objects
 */
public final class ExtendFeedbackDAO implements IExtendFeedbackDAO
{
	// Constants
	private static final String SQL_QUERY_SELECT = "SELECT id, id_history, id_resource, resource_type, comment, update_status_date, status FROM extend_feedback WHERE id = ?";
	private static final String SQL_QUERY_SELECT_BY_ID_AND_TYPE_RESOURCE = "SELECT id, id_history, id_resource, resource_type, comment, update_status_date, status FROM extend_feedback WHERE id_resource = ? AND resource_type = ?";
	private static final String SQL_QUERY_INSERT = "INSERT INTO extend_feedback ( id_history, id_resource, resource_type, comment, update_status_date ) VALUES ( ?, ?, ?, ?, ? ) ";
	private static final String SQL_QUERY_DELETE = "DELETE FROM extend_feedback WHERE id = ? ";
	private static final String SQL_QUERY_UPDATE = "UPDATE extend_feedback SET id = ?, id_resource = ?, resource_type = ?, comment = ?, update_status_date = ?, status = ? WHERE id = ?";
	private static final String SQL_QUERY_SELECTALL = "SELECT id, id_history, id_resource, resource_type, comment, update_status_date, status FROM extend_feedback";

    // FILTER
	private static final String SQL_FILTER_WHERE = " WHERE ";
	private static final String SQL_FILTER_AND = " AND ";
	private static final String SQL_FILTER_STATUS = " status = ";
	private static final String SQL_FILTER_ID_RESOURCE = " id_resource = ";
	private static final String SQL_FILTER_RESOURCE_TYPE = " resource_type = ";
	private static final String SQL_FILTER_ORDER_BY = " ORDER BY update_status_date ";
	
	
    /**
     * {@inheritDoc }
     */
    @Override
	public void insert( ExtendFeedback extendFeedback, Plugin plugin )
	{
		try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT , Statement.RETURN_GENERATED_KEYS, plugin ) )
		{              
            int nIndex = 1;
            daoUtil.setLong ( nIndex++, extendFeedback.getResourceExtenderHistory( ).getIdHistory( ) );
            daoUtil.setInt ( nIndex++, extendFeedback.getIdResource ( ) );
            daoUtil.setString ( nIndex++, extendFeedback.getResourceType ( ) );
            daoUtil.setString ( nIndex++, extendFeedback.getComment ( ) );
            daoUtil.setTimestamp( nIndex++, new Timestamp( new Date( ).getTime( ) ) );
            
            daoUtil.executeUpdate();
            if ( daoUtil.nextGeneratedKey() ) 
            {
                extendFeedback.setId( daoUtil.getGeneratedKeyInt( 1 ) );
            }
            daoUtil.free( ); 
		}
    }

        
    /**
     * {@inheritDoc }
     */
    @Override
    public ExtendFeedback load( int nId, Plugin plugin )
    {
		try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT , plugin ) )
		{
			daoUtil.setInt( 1 , nId );
			daoUtil.executeQuery();
		
			ExtendFeedback extendFeedback = null;
			int nIndex = 1;
			if ( daoUtil.next() )
			{
				extendFeedback = new ExtendFeedback();
				IResourceExtenderHistoryService resoucrExtenderHistoryService = SpringContextService.getBean( ResourceExtenderHistoryService.BEAN_SERVICE );
                extendFeedback.setId( daoUtil.getInt( nIndex++ ) );
                ResourceExtenderHistory resourceExtenderHistory = resoucrExtenderHistoryService.findByPrimary( daoUtil.getInt(  nIndex++ ) );
                extendFeedback.setResourceExtenderHistory( resourceExtenderHistory );
                extendFeedback.setIdResource( daoUtil.getInt(  nIndex++ ) );
                extendFeedback.setResourceType( daoUtil.getString(  nIndex++ ) );
                extendFeedback.setComment( daoUtil.getString(  nIndex++ ) );
                extendFeedback.setUpdateStatusDate( daoUtil.getTimestamp( nIndex++ ) );
                extendFeedback.setStatus( daoUtil.getBoolean( nIndex++ ) );
                
			}
		
			daoUtil.free();
			return extendFeedback;
		}
    }

        
    /**
     * {@inheritDoc }
     */
    @Override
	public void delete( int nExtendFeedbackId, Plugin plugin )
	{
		try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE , plugin ) )
		{
			daoUtil.setInt( 1 , nExtendFeedbackId );
			daoUtil.executeUpdate();
			daoUtil.free();
		}
	}

        
    /**
     * {@inheritDoc }
     */
    @Override
	public void store( ExtendFeedback extendFeedback, Plugin plugin )
	{
		try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE , plugin ) )
		{                
            int nIndex = 1;
            daoUtil.setInt( nIndex++, extendFeedback.getId( ) );
            daoUtil.setInt( nIndex++, extendFeedback.getIdResource( ) );
            daoUtil.setString( nIndex++, extendFeedback.getResourceType( ) );
            daoUtil.setString( nIndex++, extendFeedback.getComment( ) );
            daoUtil.setInt( nIndex++, extendFeedback.getId( ) );
            daoUtil.setTimestamp( nIndex++, extendFeedback.getUpdateStatusDate ( ) );
            daoUtil.setBoolean( nIndex++, extendFeedback.isStatus ( ) );
            
			daoUtil.executeUpdate( );
			daoUtil.free( );
		}
	}

        
    /**
     * {@inheritDoc }
     */
    @Override
    public List<ExtendFeedback> selectExtendFeedbacksList( Plugin plugin )
	{
		try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL , plugin ) )
		{
			List<ExtendFeedback> listExtendFeedbacks = new ArrayList<>(  );
			daoUtil.executeQuery(  );
			IResourceExtenderHistoryService resoucrExtenderHistoryService = SpringContextService.getBean( ResourceExtenderHistoryService.BEAN_SERVICE );
			while ( daoUtil.next(  ) )
			{
				int nIndex = 1;
                ExtendFeedback extendFeedback = new ExtendFeedback(  );
                extendFeedback.setId( daoUtil.getInt( nIndex++ ) );
                ResourceExtenderHistory resourceExtenderHistory = resoucrExtenderHistoryService.findByPrimary( daoUtil.getInt(  nIndex++ ) );
                extendFeedback.setResourceExtenderHistory( resourceExtenderHistory );
                extendFeedback.setIdResource( daoUtil.getInt( nIndex++ ) );
                extendFeedback.setResourceType( daoUtil.getString( nIndex++ ) );
                extendFeedback.setComment( daoUtil.getString( nIndex++ ) );
                extendFeedback.setUpdateStatusDate( daoUtil.getTimestamp( nIndex++ ) );
                extendFeedback.setStatus( daoUtil.getBoolean( nIndex++ ) );
                
                listExtendFeedbacks.add( extendFeedback );
			}
	
			daoUtil.free();
			return listExtendFeedbacks;
		}
	}

    /**
     * {@inheritDoc }
     */
	@Override
	public List<ExtendFeedback> findAllByIdAndTypeResource(int nIdResource, String strResourceType, Plugin plugin)
	{
		try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ID_AND_TYPE_RESOURCE , plugin ) )
		{
			daoUtil.setInt( 1 , nIdResource );
			daoUtil.setString( 2 , strResourceType );
			daoUtil.executeQuery();
		
			List<ExtendFeedback> listExtendFeedbacks = new ArrayList<>(  );
			IResourceExtenderHistoryService resoucrExtenderHistoryService = SpringContextService.getBean( ResourceExtenderHistoryService.BEAN_SERVICE );
			while ( daoUtil.next(  ) )
			{
				int nIndex = 1;
                ExtendFeedback extendFeedback = new ExtendFeedback(  );
                extendFeedback.setId( daoUtil.getInt( nIndex++ ) );
                ResourceExtenderHistory resourceExtenderHistory = resoucrExtenderHistoryService.findByPrimary( daoUtil.getInt(  nIndex++ ) );
                extendFeedback.setResourceExtenderHistory( resourceExtenderHistory );
                extendFeedback.setIdResource( daoUtil.getInt( nIndex++ ) );
                extendFeedback.setResourceType( daoUtil.getString( nIndex++ ) );
                extendFeedback.setComment( daoUtil.getString( nIndex++ ) );
                extendFeedback.setUpdateStatusDate( daoUtil.getTimestamp( nIndex++ ) );
                extendFeedback.setStatus( daoUtil.getBoolean( nIndex++ ) );
                
                listExtendFeedbacks.add( extendFeedback );
			}
	
			daoUtil.free();
			return listExtendFeedbacks;
		}
	}


	@Override
	public List<ExtendFeedback> selectExtendFeedbacksList( String strStatus, String strSorting,
			String extendableResourceTypeFilter, ResourceExtenderDTO resourceExtenderDTO, Plugin plugin )
	{
		StringBuilder request = constructRequest(strStatus, strSorting, extendableResourceTypeFilter,
				resourceExtenderDTO);
		
		try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL + request.toString( ) , plugin ) )
		{
			daoUtil.executeQuery();
		
			List<ExtendFeedback> listExtendFeedbacks = new ArrayList<>(  );
			IResourceExtenderHistoryService resoucrExtenderHistoryService = SpringContextService.getBean( ResourceExtenderHistoryService.BEAN_SERVICE );
			while ( daoUtil.next(  ) )
			{
				int nIndex = 1;
                ExtendFeedback extendFeedback = new ExtendFeedback(  );
                extendFeedback.setId( daoUtil.getInt( nIndex++ ) );
                ResourceExtenderHistory resourceExtenderHistory = resoucrExtenderHistoryService.findByPrimary( daoUtil.getInt(  nIndex++ ) );
                extendFeedback.setResourceExtenderHistory( resourceExtenderHistory );
                extendFeedback.setIdResource( daoUtil.getInt( nIndex++ ) );
                extendFeedback.setResourceType( daoUtil.getString( nIndex++ ) );
                extendFeedback.setComment( daoUtil.getString( nIndex++ ) );
                extendFeedback.setUpdateStatusDate( daoUtil.getTimestamp( nIndex++ ) );
                extendFeedback.setStatus( daoUtil.getBoolean( nIndex++ ) );
                
                listExtendFeedbacks.add( extendFeedback );
			}
	
			daoUtil.free();
			return listExtendFeedbacks;
		}

	}


	/**
	 * Construct request
	 * @param strStatus
	 * @param strSorting
	 * @param extendableResourceTypeFilter
	 * @param resourceExtenderDTO
	 * @return request
	 */
	private StringBuilder constructRequest(String strStatus, String strSorting, String extendableResourceTypeFilter,
			ResourceExtenderDTO resourceExtenderDTO)
	{
		StringBuilder request = new StringBuilder( );
		
		if ( !FeedbackConstants.STAR.equals( resourceExtenderDTO.getIdExtendableResource( ) ) )
		{
			if ( request.length() < 1 )
			{
				request.append( SQL_FILTER_WHERE );
			}
			request.append( SQL_FILTER_ID_RESOURCE + resourceExtenderDTO.getIdExtendableResource( ) );
		}
		
		if( !FeedbackConstants.STAR.equals( strStatus ) && StringUtils.isNotEmpty( strStatus ) )
		{
			if ( request.length() < 1 )
			{
				request.append( SQL_FILTER_WHERE );
			}
			else 
			{
				request.append( SQL_FILTER_AND );
			}
			request.append( SQL_FILTER_STATUS + strStatus );
		}
		
		if( !FeedbackConstants.STAR.equals( extendableResourceTypeFilter ) && StringUtils.isNotEmpty( extendableResourceTypeFilter ) )
		{
			if ( request.length() < 1 )
			{
				request.append( SQL_FILTER_WHERE );
			}
			else 
			{
				request.append( SQL_FILTER_AND );
			}
			request.append( SQL_FILTER_RESOURCE_TYPE + "'" + extendableResourceTypeFilter + "'");
		}
		
		if( !FeedbackConstants.STAR.equals( strSorting ) && StringUtils.isNotEmpty( strSorting ) )
		{
			request.append( SQL_FILTER_ORDER_BY + strSorting );
		}
		return request;
	}

}
